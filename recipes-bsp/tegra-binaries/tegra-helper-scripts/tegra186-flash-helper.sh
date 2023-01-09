#!/bin/bash
bup_blob=0
rcm_boot=0
keyfile=
sbk_keyfile=
user_keyfile=
external_device=0
no_flash=0
to_sign=0
flash_cmd=
imgfile=
dataimg=
bootpartimg=
inst_args=""
extdevargs=
make_sdcard_args=
blocksize=4096

ARGS=$(getopt -n $(basename "$0") -l "bup,no-flash,sign,external-device,rcm-boot,datafile:,bootpartfile:,usb-instance:,user_key:" -o "u:v:s:b:B:yc:" -- "$@")
if [ $? -ne 0 ]; then
    echo "Error parsing options" >&2
    exit 1
fi
eval set -- "$ARGS"
unset ARGS

while true; do
    case "$1" in
	--bup)
	    bup_blob=1
	    no_flash=1
	    shift
	    ;;
	--no-flash)
	    no_flash=1
	    shift
	    ;;
	--sign)
	    to_sign=1
	    shift
	    ;;
	--rcm-boot)
	    rcm_boot=1
	    shift
	    ;;
	--external-device)
	    external_device=1
	    extdevargs="--external_device"
	    shift
	    ;;
	--datafile)
	    dataimg="$2"
	    shift 2
	    ;;
	--bootpartfile)
	    bootpartimg="$2"
	    shift 2
	    ;;
	--usb-instance)
	    usb_instance="$2"
	    inst_args="--instance ${usb_instance}"
	    shift 2
	    ;;
	--user_key)
	    user_keyfile="$2"
	    shift 2
	    ;;
	-u)
	    keyfile="$2"
	    shift 2
	    ;;
	-v)
	    sbk_keyfile="$2"
	    shift 2
	    ;;
	-B)
	    blocksize="$2"
	    shift 2
	    ;;
	-s)
	    make_sdcard_args="$make_sdcard_args -s $2"
	    shift 2
	    ;;
	-b)
	    make_sdcard_args="$make_sdcard_args -b $2"
	    shift 2
	    ;;
	-c)
	    flash_cmd="$2"
	    shift 2
	    ;;
	--)
	    shift
	    break
	    ;;
	*)
	    echo "Error processing options" >&2
	    exit 1
	    ;;
    esac
done

flash_in="$1"
dtb_file="$2"
sdramcfg_file="$3"
odmdata="$4"
kernfile="$5"
imgfile="$6"
shift 6

here=$(readlink -f $(dirname "$0"))
flashappname="tegraflash.py"

if [ ! -e ./flashvars ]; then
    echo "ERR: missing flash variables file" >&2
    exit 1
fi

. ./flashvars

if [ -z "$FLASHVARS" ]; then
    echo "ERR: flash variable set not defined" >&2
    exit 1
fi

# Temp file for storing cvm.bin in, if we need to query the board for its
# attributes
cvm_bin=$(mktemp cvm.bin.XXXXX)

skipuid=""
BR_CID=
have_boardinfo=
if [ -z "$BOARDID" -o -z "$FAB" ]; then
    BR_CID=$($here/tegrarcm_v2 ${inst_args} --uid | grep BR_CID | cut -d' ' -f2)
    if [ "${BR_CID:3:2}" != "18" ]; then
	echo "ERR: chip ID mismatch for TX2" >&2
    fi
    keyargs=
    [ -z "$keyfile" ] || keyargs="$keyargs --key $keyfile"
    [ -z "$sbk_keyfile" ] || keyargs="$keyargs --encrypt_key $sbk_keyfile"
    if ! python3 "$flashappname" ${inst_args} --skipuid $keyargs --chip 0x18 --applet mb1_recovery_prod.bin --cmd "dump eeprom boardinfo ${cvm_bin}"; then
	echo "ERR: could not retrieve EEPROM board information" >&2
	exit 1
    fi
    have_boardinfo=yes
fi
if [ -n "$BOARDID" ]; then
    boardid="$BOARDID"
else
    boardid=`$here/chkbdinfo -i ${cvm_bin} | tr -d '[:space:]'`
    BOARDID="$boardid"
fi

if [ -n "$FAB" ]; then
    board_version="$FAB"
else
    board_version=`$here/chkbdinfo -f ${cvm_bin} | tr -d '[:space:]' | tr [a-z] [A-Z]`
    FAB="$board_version"
fi

if [ -z "$BOARDSKU" -a -n "$have_boardinfo" ]; then
    BOARDSKU=`$here/chkbdinfo -k ${cvm_bin} | tr -d '[:space:]' | tr [a-z] [A-Z]`
fi
if [ -z "$BOARDREV" -a -n "$have_boardinfo" ]; then
    BOARDREV=`$here/chkbdinfo -r ${cvm_bin} | tr -d '[:space:]' | tr [a-z] [A-Z]`
fi
if [ -z "$serial_number" -a -n "$have_boardinfo" ]; then
    serial_number=$($here/chkbdinfo -a ${cvm_bin} | tr -d '[:space:]')
fi
[ -n "$fuselevel" ] || fuselevel=fuselevel_production

[ -f ${cvm_bin} ] && rm -f ${cvm_bin}

rm -f boardvars.sh
cat >boardvars.sh <<EOF
BOARDID="$BOARDID"
FAB="$FAB"
fuselevel="$fuselevel"
EOF
if [ -n "$BOARDSKU" ]; then
    echo "BOARDSKU=$BOARDSKU" >>boardvars.sh
fi
if [ -n "$BOARDREV" ]; then
    echo "BOARDREV=$BOARDREV" >>boardvars.sh
fi
if [ -n "$serial_number" ]; then
    echo "serial_number=$serial_number" >>boardvars.sh
fi
if [ -n "$usb_instance" ]; then
    echo "usb_instance=$usb_instance" >>boardvars.sh
fi
if [ -n "$BR_CID" ]; then
    echo "BR_CID=\"$BR_CID\"" >>boardvars.sh
fi

# The following defaults are drawn from
# p2771-0000.conf.common in the L4T kit.
if [ "$boardid" = "3310" ]; then
    # The B00 revision SOM which shipped with at least some
    # Jetson TX2 dev kits.
    # TOREV is used for all substitutions, except for
    # BPFDTB and PMIC revisions, which differ between B00
    # and B01 revisions.
    TOREV="c03"
    BPFDTBREV="c01"
    PMICREV="c03"

    case $board_version in
        B0[1-9]|[C-Z]??)
            BPFDTBREV="c04"
            PMICREV="c04"
            ;;
        B00)
            ;;
        *)
            echo "ERR: unsupported board version: $board_version" >&2
            exit 1
            ;;
    esac
elif [ "$boardid" = "3489" ]; then
    TOREV="a00"
    PMICREV="a00"
    BPFDTBREV="a00";
    if [[ "$board_version" < "300" ]]; then
        BPFDTBREV="evt"
    fi
elif [ "$boardid" = "3636" ]; then
    TOREV="a00"
    PMICREV="a00"
    BPFDTBREV="a00";
else
    echo "ERR: unsupported board id: $boardid" >&2
    exit 1
fi

for var in $FLASHVARS; do
    eval pat=$`echo $var`
    if [ -z "$pat" ]; then
	echo "ERR: missing variable: $var" >&2
	exit 1
    fi
    eval $var=`echo $pat | sed -e"s,@BPFDTBREV@,$BPFDTBREV," -e"s,@BOARDREV@,$TOREV," -e"s,@PMICREV@,$PMICREV,"`
done

[ -n "$BOARDID" ] || BOARDID=3310
case "$BOARDID" in
    3310)
	[ -n "$BOARDSKU" ] || BOARDSKU=1000
	bup_boardsku=
	;;
    3489)
	[ -n "$BOARDSKU" ] || BOARDSKU=0000
	bup_boardsku=
	;;
    3636)
	[ -n "$BOARDSKU" ] || BOARDSKU=0001
	bup_boardsku="$BOARDSKU"
	;;
    *)
	echo "ERR: no default board SKU for board ID $BOARDID" >&2
	exit 1
	;;
esac
[ -n "$BOOTDEV" ] || BOOTDEV="mmcblk0p1"

rm -f ${MACHINE}_bootblob_ver.txt
echo "NV3" >${MACHINE}_bootblob_ver.txt
. bsp_version
echo "# R$BSP_BRANCH , REVISION: $BSP_MAJOR.$BSP_MINOR" >>${MACHINE}_bootblob_ver.txt
echo "BOARDID=$BOARDID BOARDSKU=$BOARDSKU FAB=$FAB" >>${MACHINE}_bootblob_ver.txt
date "+%Y%m%d%H%M%S" >>${MACHINE}_bootblob_ver.txt
bytes=`cksum ${MACHINE}_bootblob_ver.txt | cut -d' ' -f2`
cksum=`cksum ${MACHINE}_bootblob_ver.txt | cut -d' ' -f1`
echo "BYTES:$bytes CRC32:$cksum" >>${MACHINE}_bootblob_ver.txt
if [ $external_device -eq 0 ]; then
    appfile=$(basename "$imgfile").img
    if [ -n "$dataimg" ]; then
	datafile=$(basename "$dataimg").img
    fi
    if [ -n "$bootpartimg" ]; then
	bootpartfile=$(basename "$bootpartimg").img
    fi
else
    appfile="$imgfile"
    datafile="$dataimg"
    bootpartfile="$bootpartimg"
fi
appfile_sed=
if [ $bup_blob -ne 0 -o $rcm_boot -ne 0 ]; then
    kernfile="${kernfile:-boot.img}"
    appfile_sed="-e/APPFILE/d -e/DATAFILE/d -e/BOOTPARTFILE/d"
elif [ $no_flash -eq 0 -a $external_device -eq 0 ]; then
    appfile_sed="-es,APPFILE_b,$appfile, -es,APPFILE,$appfile, -es,DATAFILE,$datafile, -es,BOOTPARTFILE,$bootpartfile,"
elif [ $no_flash -ne 0 ]; then
    touch APPFILE APPFILE_b DATAFILE BOOTPARTFILE
else
    pre_sdcard_sed="-es,APPFILE,$appfile,"
    if [ -n "$datafile" ]; then
	pre_sdcard_sed="$pre_sdcard_sed -es,DATAFILE,$datafile,"
    fi
    if [ -n "$bootpartfile" ]; then
	pre_sdcard_sed="$pre_sdcard_sed -es,BOOTPARTFILE,$bootpartfile,"
    fi
fi

dtb_file_basename=$(basename "$dtb_file")
kernel_dtbfile="kernel_$dtb_file_basename"
rm -f "$kernel_dtbfile"
cp "$dtb_file" "$kernel_dtbfile"

sed -e"s,VERFILE,${MACHINE}_bootblob_ver.txt," -e"s,BPFDTB-FILE,$BPFDTB_FILE," \
    -e"s,TBCDTB-FILE,$dtb_file," -e"s,KERNELDTB-FILE,$kernel_dtbfile," -e"s, DTB_FILE,$kernel_dtbfile," \
    $appfile_sed "$flash_in" > flash.xml

BINSARGS="mb2_bootloader nvtboot_recovery.bin; \
mts_preboot preboot_d15_prod_cr.bin; \
mts_bootpack mce_mts_d15_prod_cr.bin; \
bpmp_fw bpmp.bin; \
bpmp_fw_dtb $BPFDTB_FILE; \
tlk tos-trusty.img; \
eks eks.img; \
bootloader_dtb $dtb_file"

have_odmsign_func=0
[ ! -e "$here/odmsign.func" ] || have_odmsign_func=1
if [ -n "$keyfile" -o -n "$sbk_keyfile" -o -n "$user_keyfile" ] && [ $have_odmsign_func -eq 0 ]; then
    echo "ERR: missing odmsign.func from secureboot package, signing not supported" >&2
    exit 1
fi
if [ $rcm_boot -ne 0 ]; then
    BINSARGS="$BINSARGS; kernel $kernfile; kernel_dtb $kernel_dtbfile; sce_fw camera-rtcpu-sce.img adsp_fw adsp-fw.bin"
fi

cfgappargs="--sdram_config $sdramcfg_file \
              --applet mb1_recovery_prod.bin"
bctargs="--misc_config $MISC_CONFIG \
	      --pinmux_config $PINMUX_CONFIG \
	      --pmic_config $PMIC_CONFIG \
	      --pmc_config $PMC_CONFIG \
	      --prod_config $PROD_CONFIG \
	      --scr_config $SCR_CONFIG \
	      --scr_cold_boot_config $SCR_COLD_BOOT_CONFIG \
	      --br_cmd_config $BOOTROM_CONFIG \
	      --dev_params $DEV_PARAMS"

if [ $bup_blob -ne 0 -o $to_sign -ne 0 -o $external_device -eq 1 ]; then
    tfcmd=sign
    skipuid="--skipuid"
elif [ $rcm_boot -ne 0 ]; then
    tfcmd=rcmboot
else
    if [ $external_device -eq 0 -a $no_flash -eq 0 ]; then
	rm -f "$appfile"
	$here/mksparse -b ${blocksize} --fillpattern=0 "$imgfile" "$appfile" || exit 1
	if [ -n "$datafile" ]; then
	    rm -f "$datafile"
	    $here/mksparse -b ${blocksize} --fillpattern=0 "$dataimg" "$datafile" || exit 1
	fi
	if [ -n "$bootpartfile" ]; then
	    rm -f "$bootpartfile"
	    $here/mksparse -b ${blocksize} --fillpattern=0 "$bootpartimg" "$bootpartfile" || exit 1
	fi
    fi
    tfcmd=${flash_cmd:-"flash;reboot"}
fi

temp_user_dir=
want_signing=0
if [ -n "$keyfile" ] || [ $rcm_boot -eq 1 ] || [ $no_flash -eq 1 -a $to_sign -eq 1 ]; then
    want_signing=1
fi
if [ $have_odmsign_func -eq 1 -a $want_signing -eq 1 ]; then
    if [ -n "$sbk_keyfile" ]; then
	if [ -z "$user_keyfile" ]; then
	    rm -f "null_user_key.txt"
	    echo "0x00000000 0x00000000 0x00000000 0x00000000" > null_user_key.txt
	    user_keyfile=$(readlink -f null_user_key.txt)
	fi
	rm -rf signed_bootimg_dir
	mkdir signed_bootimg_dir
	cp "$kernfile" "$kernel_dtbfile" signed_bootimg_dir/
	oldwd="$PWD"
	cd signed_bootimg_dir
	if [ -x $here/l4t_sign_image.sh ]; then
	    signimg="$here/l4t_sign_image.sh";
	else
	    hereparent=$(readlink -f "$here/.." 2>/dev/null)
	    if [ -n "$hereparent" -a -x "$hereparent/l4t_sign_image.sh" ]; then
		signimg="$hereparent/l4t_sign_image.sh"
	    fi
	fi
	if [ -z "$signimg" ]; then
	    echo "ERR: missing l4t_sign_image script" >&2
	    exit 1
	fi
	"$signimg" --file "$kernfile"  --key "$keyfile" --encrypt_key "$user_keyfile" --chip 0x18 --split False &&
	    "$signimg" --file "$kernel_dtbfile"  --key "$keyfile" --encrypt_key "$user_keyfile" --chip 0x18 --split False
	rc=$?
	cd "$oldwd"
	if [ $rc -ne 0 ]; then
	    echo "Error signing kernel image or device tree" >&2
	    exit 1
	fi
	temp_user_dir=signed_bootimg_dir
    fi
    CHIPID="0x18"
    tegraid="$CHIPID"
    localcfgfile="flash.xml"
    dtbfilename="$kernel_dtbfile"
    tbcdtbfilename="$dtb_file"
    bpfdtbfilename="$BPFDTB_FILE"
    localbootfile="$kernfile"
    BINSARGS="--bins \"$BINSARGS\""
    flashername=nvtboot_recovery_cpu.bin
    BCT="--sdram_config"
    bctfilename="$sdramcfg_file"
    SOSARGS="--applet mb1_recovery_prod.bin "
    BCTARGS="$bctargs"
    rootfs_ab=0
    . "$here/odmsign.func"
    (odmsign_ext_sign_and_flash) || exit 1
    if [ $bup_blob -eq 0 -a $no_flash -ne 0 ]; then
	if [ -f flashcmd.txt ]; then
	    chmod +x flashcmd.txt
	    ln -sf flashcmd.txt ./secureflash.sh
	else
	    echo "WARN: signing completed successfully, but flashcmd.txt missing" >&2
	fi
	rm -f APPFILE APPFILE_b DATAFILE BOOTPARTFILE null_user_key.txt
    fi
    if [ $bup_blob -eq 0 ]; then
	if [ -n "$temp_user_dir" ]; then
	    cp "$temp_user_dir"/*.encrypt.signed .
	    rm -rf "$temp_user_dir"
	fi
	exit 0
    fi
    touch odmsign.func
fi

flashcmd="python3 $flashappname ${inst_args} --chip 0x18 --bl nvtboot_recovery_cpu.bin \
	      $cfgappargs \
	      --odmdata $odmdata \
	      --cmd \"$tfcmd\" $skipuid \
	      --cfg flash.xml \
	      $bctargs $extdevargs \
	      --bins \"$BINSARGS\""

if [ $bup_blob -ne 0 ]; then
    [ -z "$keyfile" ] || flashcmd="${flashcmd} --key \"$keyfile\""
    [ -z "$sbk_keyfile" ] || flashcmd="${flashcmd} --encrypt_key \"$sbk_keyfile\""
    support_multi_spec=1
    clean_up=0
    dtbfilename="$kernel_dtbfile"
    tbcdtbfilename="$dtb_file"
    bpfdtbfilename="$BPFDTB_FILE"
    localbootfile="boot.img"
    . "$here/l4t_bup_gen.func"
    spec="${BOARDID}-${FAB}-${bup_boardsku}--1-0-${MACHINE}-${BOOTDEV}"
    if [ $(expr length "$spec") -ge 64 ]; then
	echo "ERR: TNSPEC must be shorter than 64 characters: $spec" >&2
	exit 1
    fi
    l4t_bup_gen "$flashcmd" "$spec" "$fuselevel" t186ref "$keyfile" "$sbk_keyfile" 0x18 || exit 1
    exit 0
fi

if [ $to_sign -ne 0 ]; then
    eval $flashcmd < /dev/null || exit 1
    exit 0
fi

if [ $no_flash -ne 0 ]; then
    echo "$flashcmd" | sed -e 's,--skipuid,,g' > flashcmd.txt
    chmod +x flashcmd.txt
    rm -f APPFILE APPFILE_b DATAFILE BOOTPARTFILE null_user_key.txt
else
    eval $flashcmd < /dev/null || exit 1
    if [ $external_device -eq 1 ]; then
	if [ -z "$serial_number" ]; then
	    echo "ERR: missing serial number for initrd-flashing external device" >&2
	    exit 1
	fi
	make_sdcard_args="$make_sdcard_args --serial-number $serial_number"
	if [ -n "$pre_sdcard_sed" ]; then
	    rm -f signed/flash.xml.tmp.in
	    mv signed/flash.xml.tmp signed/flash.xml.tmp.in
	    sed $pre_sdcard_sed  signed/flash.xml.tmp.in > signed/flash.xml.tmp
	fi
	$here/make-sdcard $make_sdcard_args signed/flash.xml.tmp "$@"
    fi
fi
