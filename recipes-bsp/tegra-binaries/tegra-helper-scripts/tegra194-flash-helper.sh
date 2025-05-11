#!/bin/bash
bup_blob=0
bup_type=
rcm_boot=0
keyfile=
sbk_keyfile=
spi_only=
external_device=0
sdcard=
no_flash=0
to_sign=0
flash_cmd=
imgfile=
dataimg=
inst_args=""
extdevargs=
blocksize=4096

# These functions are used in odmsign.func but do not
# need to do anything when run from this script, as we
# have already copied needed files to the current working
# directory.
mkfilesoft() {
    :
}

cp2local() {
    :
}

signimage() {
    :
}

process_l4t_conf_dtbo() {
    :
}

partition_exists_in_PT_table() {
    [ "$1" = "BCT-boot-chain_backup" ]
}

ARGS=$(getopt -n $(basename "$0") -l "bup,bup-type:,no-flash,sign,sdcard,spi-only,boot-only,external-device,rcm-boot,datafile:,usb-instance:,uefi-enc:" -o "u:v:s:b:B:yc:" -- "$@")
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
	--bup-type)
	    bup_type="$2"
	    shift 2
	    ;;
	--no-flash)
	    no_flash=1
	    shift
	    ;;
	--sign)
	    to_sign=1
	    shift
	    ;;
	--sdcard)
	    sdcard=yes
	    shift
	    ;;
	--spi-only|--boot-only)
	    spi_only=yes
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
	--usb-instance)
	    usb_instance="$2"
	    inst_args="--instance ${usb_instance}"
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
	-s)
	    make_sdcard_args="$make_sdcard_args -s $2"
	    shift 2
	    ;;
	-b)
	    make_sdcard_args="$make_sdcard_args -b $2"
	    shift 2
	    ;;
	-B)
	    blocksize="$2"
	    shift 2
	    ;;
	-y)
	    make_sdcard_args="$make_sdcard_args -y"
	    shift
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
sdramcfg_files="$3"
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
bootauth=""
BR_CID=
if [ -z "$CHIPREV" -o -z "$fuselevel" ]; then
    BR_CID=$($here/tegrarcm_v2 ${inst_args} --uid | grep BR_CID | cut -d' ' -f2)
    chipid="$BR_CID"
    if [ -z "$chipid" ]; then
	echo "ERR: could not retrieve chip ID" >&2
	exit 1
    fi
    if [ "${chipid:3:2}" != "80" -o "${chipid:6:2}" != "19" ]; then
	echo "ERR: chip ID mismatch for Xavier" >&2
	exit 1
    fi
    case "${chipid:2:1}" in
	8)
	    fuselevel="fuselevel_production"
	    bootauth="NS"
	    ;;
	9|a)
	    fuselevel="fuselevel_production"
	    bootauth="PKC"
	    ;;
	d|e)
	    fuselevel="fuselevel_production"
	    bootauth="SBKPKC"
	    ;;
	*)
	    echo "ERR: non-production chip found" >&2
	    exit 1
	    ;;
    esac
    CHIPREV="${chipid:5:1}"
    skipuid="--skipuid"
    case $bootauth in
        PKC|SBKPKC)
            if [ -z "$keyfile" -o -z "$sbk_keyfile" ]; then
                echo "ERR: Target is configured for secure boot ($bootauth); use -u and -v options to specify key files" >&2
                exit 1
            fi
            ;;
        NS)
            if [ -n "$keyfile" -o -n "$sbk_keyfile" ]; then
                echo "WARN: Target is not secured; ignoring key files" >&2
                keyfile=
                sbk_keyfile=
            fi
            ;;
    esac
fi

have_boardinfo=
if [ -z "$FAB" -o -z "$BOARDID" ]; then
    keyargs=
    [ -z "$keyfile" ] || keyargs="$keyargs --key $keyfile"
    [ -z "$sbk_keyfile" ] || keyargs="$keyargs --encrypt_key $sbk_keyfile"
    rm -f rcm_state
    if ! python3 $flashappname ${inst_args} --chip 0x19 --applet mb1_t194_prod.bin $skipuid --soft_fuses tegra194-mb1-soft-fuses-l4t.cfg \
		 --bins "mb2_applet nvtboot_applet_t194.bin" --cmd "dump eeprom boardinfo ${cvm_bin};reboot recovery" $keyargs; then
	echo "ERR: could not retrieve EEPROM board information" >&2
	exit 1
    fi
    have_boardinfo="yes"
    skipuid=""
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
if [ -n "$BOARDSKU" ]; then
    board_sku="$BOARDSKU"
elif [ -n "$have_boardinfo" ]; then
    board_sku=`$here/chkbdinfo -k ${cvm_bin} | tr -d '[:space:]' | tr [a-z] [A-Z]`
    BOARDSKU="$board_sku"
fi
if [ -n "$BOARDREV" ]; then
    board_revision="$BOARDREV"
elif [ -n "$have_boardinfo" ]; then
    board_revision=`$here/chkbdinfo -r ${cvm_bin} | tr -d '[:space:]' | tr [a-z] [A-Z]`
    BOARDREV="$board_revision"
fi
if [ -z "$serial_number" -a -n "$have_boardinfo" ]; then
    serial_number=$($here/chkbdinfo -a ${cvm_bin} | tr -d '[:space:]')
fi

[ -f ${cvm_bin} ] && rm -f ${cvm_bin}

rm -f boardvars.sh
cat >boardvars.sh <<EOF
BOARDID="$BOARDID"
FAB="$FAB"
BOARDSKU="$BOARDSKU"
BOARDREV="$BOARDREV"
CHIPREV="$CHIPREV"
fuselevel="$fuselevel"
EOF
if [ -n "$serial_number" ]; then
    echo "serial_number=$serial_number" >>boardvars.sh
fi
if [ -n "$usb_instance" ]; then
    echo "usb_instance=$usb_instance" >>boardvars.sh
fi
if [ -n "$BR_CID" ]; then
    echo "BR_CID=\"$BR_CID\"" >>boardvars.sh
fi

# Adapted from p2972-0000.conf.common in L4T kit
TOREV="a01"
BPFDTBREV="a01"
PMICREV="a01"

case "$boardid" in
    2888)
	case $board_version in
	    [01][0-9][0-9])
	    ;;
	    2[0-9][0-9])
		TOREV="a02"
		PMICREV="a02"
		BPFDTBREV="a02"
		;;
	    [34][0-9][0-9])
		TOREV="a02"
		PMICREV="a04"
		BPFDTBREV="a02"
		if [ $board_sku -ge 4 ] || [ $board_version -gt 300 -a `expr "$board_revision" \> "D.0"` -eq 1 ]; then
		    PMICREV="a04-E-0"
		    BPFDTBREV="a04"
		fi
		;;
		[06][0-9][0-9]) # 600 range is the board version of the Jetson AGX Xavier Industrial
		;;
	    *)
		echo "ERR: unrecognized board version $board_version" >&2
		exit 1
		;;
	esac
	if [ "$board_sku" = "0005" ]; then
	    # AGX Xavier 64GB
	    BPFDTBREV="0005-a04-maxn"
	fi
	;;
    3668)
	# No revision-specific settings
	;;
    *)
	echo "ERR: unrecognized board ID $boardid" >&2
	exit 1
	;;
esac

ramcodeargs=
if [ "$boardid" = "2888" -a "$board_sku" = "0008" ]; then
    # AGX Xavier Industrial
    ramcodeargs="--ramcode 1"
fi

for var in $FLASHVARS; do
    eval pat=$`echo $var`
    if [ -z "${pat+definedmaybeempty}" ]; then
	echo "ERR: missing variable: $var" >&2
	exit 1
    elif [ -n "$pat" ]; then
	val=$(echo $pat | sed -e"s,@BPFDTBREV@,$BPFDTBREV," -e"s,@BOARDREV@,$TOREV," -e"s,@PMICREV@,$PMICREV," -e"s,@CHIPREV@,$CHIPREV,")
	eval $var='$val'
    fi
done

[ -n "$BOARDID" ] || BOARDID=2888
[ -n "$FAB" ] || FAB=400
[ -n "$fuselevel" ] || fuselevel=fuselevel_production
[ -n "$BOOTDEV" ] || BOOTDEV="mmcblk0p1"

echo "Board ID($BOARDID) version($FAB) sku($BOARDSKU) revision($BOARDREV)"

rm -f ${MACHINE}_bootblob_ver.txt
echo "NV4" >${MACHINE}_bootblob_ver.txt
. bsp_version
echo "# R$BSP_BRANCH , REVISION: $BSP_MAJOR.$BSP_MINOR" >>${MACHINE}_bootblob_ver.txt
echo "BOARDID=$BOARDID BOARDSKU=$BOARDSKU FAB=$FAB" >>${MACHINE}_bootblob_ver.txt
date "+%Y%m%d%H%M%S" >>${MACHINE}_bootblob_ver.txt
printf "0x%x\n" $(( (BSP_BRANCH<<16) | (BSP_MAJOR<<8) | BSP_MINOR )) >>${MACHINE}_bootblob_ver.txt
bytes=$(wc -c ${MACHINE}_bootblob_ver.txt | cut -d' ' -f1)
cksum=$(python3 -c "import zlib; print(\"%X\" % (zlib.crc32(open(\"${MACHINE}_bootblob_ver.txt\", \"rb\").read()) & 0xFFFFFFFF))")
echo "BYTES:$bytes CRC32:$cksum" >>${MACHINE}_bootblob_ver.txt
if [ -z "$sdcard" -a $external_device -eq 0 ]; then
    appfile=$(basename "$imgfile").img
    if [ -n "$dataimg" ]; then
	datafile=$(basename "$dataimg").img
    fi
else
    appfile="$imgfile"
    datafile="$dataimg"
fi
appfile_sed=
if [ $bup_blob -ne 0 -o $rcm_boot -ne 0 ]; then
    kernfile="${kernfile:-boot.img}"
    appfile_sed="-e/APPFILE/d -e/DATAFILE/d"
elif [ $no_flash -eq 0 -a -z "$sdcard" -a $external_device -eq 0 ]; then
    appfile_sed="-es,APPFILE_b,$appfile, -es,APPFILE,$appfile, -es,DATAFILE,$datafile,"
elif [ $no_flash -ne 0 ]; then
    touch APPFILE APPFILE_b DATAFILE
else
    pre_sdcard_sed="-es,APPFILE_b,$appfile, -es,APPFILE,$appfile,"
    if [ -n "$datafile" ]; then
	pre_sdcard_sed="$pre_sdcard_sed -es,DATAFILE,$datafile,"
    fi
fi

dtb_file_basename=$(basename "$dtb_file")
kernel_dtbfile="kernel_$dtb_file_basename"
rm -f "$kernel_dtbfile"
if [ -e "$dtb_file.signed" ]; then
    cp "$dtb_file.signed" "$kernel_dtbfile"
else
    cp "$dtb_file" "$kernel_dtbfile"
fi

tbc_dtb_file="$TBCDTB_FILE"
if [ -z "$tbc_dtb_file" ]; then
	tbc_dtb_file="$dtb_file"
fi

if [ "$spi_only" = "yes" -o $external_device -eq 1 ]; then
    if [ ! -e "$here/nvflashxmlparse" ]; then
	echo "ERR: missing nvflashxmlparse script" >&2
	exit 1
    fi
fi
if [ "$spi_only" = "yes" ] || [ $bup_blob -ne 0 -a "$bup_type" = "bl" ]; then
    "$here/nvflashxmlparse" --extract -t boot -o flash.xml.tmp "$flash_in" || exit 1
else
    cp "$flash_in" flash.xml.tmp
fi
sed -e"s,VERFILE,${MACHINE}_bootblob_ver.txt," -e"s,BPFDTB_FILE,$BPFDTB_FILE," \
    -e"s,TBCDTB-FILE,$tbc_dtb_file," -e"s, DTB_FILE,$kernel_dtbfile," \
    $appfile_sed flash.xml.tmp > flash.xml
rm flash.xml.tmp

BINSARGS="mb2_bootloader nvtboot_recovery_t194.bin; \
mts_preboot preboot_c10_prod_cr.bin; \
mts_mce mce_c10_prod_cr.bin; \
mts_proper mts_c10_prod_cr.bin; \
bpmp_fw bpmp-2_t194.bin; \
bpmp_fw_dtb $BPFDTB_FILE; \
spe_fw spe_t194.bin; \
tos tos-optee_t194.img; \
eks eks.img; \
bootloader_dtb $tbc_dtb_file"

have_odmsign_func=0
[ ! -e "$here/odmsign.func" ] || have_odmsign_func=1
if [ -n "$keyfile" -o -n "$sbk_keyfile" ] && [ $have_odmsign_func -eq 0 ]; then
    echo "ERR: missing odmsign.func from secureboot package, signing not supported" >&2
    exit 1
fi

if [ $rcm_boot -ne 0 ]; then
    cp $kernel_dtbfile rcmboot_$kernel_dtbfile
    BINSARGS="$BINSARGS; kernel $kernfile; kernel_dtb rcmboot_$kernel_dtbfile"
fi

overlay_dtb_files="$BOOTCONTROL_OVERLAYS"
if [ -z "$overlay_dtb_files" ]; then
    overlay_dtb_files="$PLUGIN_MANAGER_OVERLAYS"
elif [ -n "$PLUGIN_MANAGER_OVERLAYS" ]; then
    overlay_dtb_files="$overlay_dtb_files,$PLUGIN_MANAGER_OVERLAYS"
fi
if [ -z "$overlay_dtb_files" ]; then
    overlay_dtb_files="$OVERLAY_DTB_FILE"
elif [ -n "$OVERLAY_DTB_FILE" ]; then
    overlay_dtb_files="$overlay_dtb_files,$OVERLAY_DTB_FILE"
fi
overlay_dtb_arg=
if [ -n "$overlay_dtb_files" ]; then
    overlay_dtb_arg="--overlay_dtb $overlay_dtb_files"
fi

bctargs="$UPHY_CONFIG $MINRATCHET_CONFIG \
         --device_config $DEVICE_CONFIG \
         --misc_config $MISC_CONFIG  \
         --misc_cold_boot_config $MISC_COLD_BOOT_CONFIG \
         --pinmux_config $PINMUX_CONFIG \
         --gpioint_config $GPIOINT_CONFIG \
         --pmic_config $PMIC_CONFIG \
         --pmc_config $PMC_CONFIG \
         --prod_config $PROD_CONFIG \
         --scr_config $SCR_CONFIG \
         --scr_cold_boot_config $SCR_COLD_BOOT_CONFIG \
         --br_cmd_config $BR_CMD_CONFIG \
         --dev_params $DEV_PARAMS,$DEV_PARAMS_B \
         $overlay_dtb_arg"


if [ $bup_blob -ne 0 -o $to_sign -ne 0 -o "$sdcard" = "yes" -o $external_device -eq 1 ]; then
    tfcmd=sign
    skipuid="--skipuid"
elif [ $rcm_boot -ne 0 ]; then
    tfcmd=rcmboot
else
    if [ -z "$sdcard" -a $external_device -eq 0 -a $no_flash -eq 0 -a "$spi_only" != "yes" ]; then
	rm -f "$appfile"
        echo "Creating sparseimage ${appfile}..."
	$here/mksparse -b ${blocksize} --fillpattern=0 "$imgfile" "$appfile" || exit 1
	if [ -n "$datafile" ]; then
	    rm -f "$datafile"
            echo "Creating sparseimage ${datafile}..."
	    $here/mksparse -b ${blocksize} --fillpattern=0 "$dataimg" "$datafile" || exit 1
	fi
    fi
    tfcmd=${flash_cmd:-"flash;reboot"}
fi

want_signing=0
if [ -n "$keyfile" ] || [ $rcm_boot -eq 1 ] || [ $no_flash -eq 1 -a $to_sign -eq 1 ]; then
    want_signing=1
fi
flashername=nvtboot_recovery_cpu_t194.bin
if [ $rcm_boot -eq 1 ]; then
    flashername=uefi_jetson.bin
fi
if [ $have_odmsign_func -eq 1 -a $want_signing -eq 1 ]; then
    CHIPID="0x19"
    tegraid="$CHIPID"
    localcfgfile="flash.xml"
    dtbfilename="$kernel_dtbfile"
    tbcdtbfilename="$tbc_dtb_file"
    bpfdtbfilename="$BPFDTB_FILE"
    localbootfile="$kernfile"
    BINSARGS="--bins \"$BINSARGS\""
    BCT="--sdram_config"
    bctfilename=`echo $sdramcfg_files | cut -d, -f1`
    bctfile1name=`echo $sdramcfg_files | cut -d, -f2`
    boot_chain_select="A"
    SOSARGS="--applet mb1_t194_prod.bin "
    NV_ARGS="--soft_fuses tegra194-mb1-soft-fuses-l4t.cfg "
    BCTARGS="$bctargs --bct_backup"
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
	rm -f APPFILE APPFILE_b DATAFILE
    fi
    if [ $bup_blob -eq 0 ]; then
	exit 0
    fi
    touch odmsign.func
fi

flashcmd="python3 $flashappname ${inst_args} --chip 0x19 --bl $flashername \
	      --sdram_config $sdramcfg_files \
	      --odmdata $odmdata \
	      --bldtb $tbc_dtb_file \
	      --applet mb1_t194_prod.bin \
	      --soft_fuses tegra194-mb1-soft-fuses-l4t.cfg \
	      --cmd \"$tfcmd\" $skipuid \
	      --cfg flash.xml \
	      --bct_backup \
	      $bctargs $ramcodeargs $extdevargs \
	      --bins \"$BINSARGS\""

if [ $bup_blob -ne 0 ]; then
    [ -z "$keyfile" ] || flashcmd="${flashcmd} --key \"$keyfile\""
    [ -z "$sbk_keyfile" ] || flashcmd="${flashcmd} --encrypt_key \"$sbk_keyfile\""
    support_multi_spec=1
    clean_up=0
    dtbfilename="$kernel_dtbfile"
    tbcdtbfilename="$tbc_dtb_file"
    bpfdtbfilename="$BPFDTB_FILE"
    localbootfile="boot.img"
    TBCFILE="uefi_jetson.bin"
    TOSFILE="tos-optee_t194.img"
    . "$here/l4t_bup_gen.func"
    spec="${BOARDID}-${FAB}-${BOARDSKU}-${BOARDREV}-1-${CHIPREV}-${MACHINE}-"
    if [ $(expr length "$spec") -ge 128 ]; then
	echo "ERR: TNSPEC must be shorter than 128 characters: $spec" >&2
	exit 1
    fi
    l4t_bup_gen "$flashcmd" "$spec" "$fuselevel" t186ref "$keyfile" "$sbk_keyfile" 0x19 || exit 1
    exit 0
fi

if [ $to_sign -ne 0 ]; then
    eval $flashcmd < /dev/null || exit 1
    exit 0
fi

if [ $no_flash -ne 0 ]; then
    echo "$flashcmd" | sed -e 's,--skipuid,,g' > flashcmd.txt
    chmod +x flashcmd.txt
    rm -f APPFILE APPFILE_b DATAFILE
else
    eval $flashcmd < /dev/null || exit 1
    if [ -n "$sdcard" -o $external_device -eq 1 ]; then
	if [ $external_device -eq 1 ]; then
	    if [ -z "$serial_number" ]; then
		echo "ERR: missing serial number for initrd-flashing external device" >&2
		exit 1
	    fi
	    make_sdcard_args="$make_sdcard_args --serial-number $serial_number"
	fi
	if [ -n "$pre_sdcard_sed" ]; then
	    rm -f signed/flash.xml.tmp.in
	    mv signed/flash.xml.tmp signed/flash.xml.tmp.in
	    sed $pre_sdcard_sed  signed/flash.xml.tmp.in > signed/flash.xml.tmp
	fi
	$here/make-sdcard $make_sdcard_args signed/flash.xml.tmp "$@"
    fi
fi
