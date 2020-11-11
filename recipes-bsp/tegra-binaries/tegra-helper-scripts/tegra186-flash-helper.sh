#!/bin/bash
bup_blob=0
keyfile=
sbk_keyfile=
no_flash=0
flash_cmd=
imgfile=
dataimg=
inst_args=""

ARGS=$(getopt -n $(basename "$0") -l "bup,no-flash,datafile:,usb-instance:" -o "u:v:c:" -- "$@")
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
	    shift
	    ;;
	--no-flash)
	    no_flash=1
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

if [ -z "$BOARDID" -o -z "$FAB" ]; then
    if ! python3 "$flashappname" ${inst_args} --chip 0x18 --applet mb1_recovery_prod.bin --cmd "dump eeprom boardinfo ${cvm_bin}"; then
	echo "ERR: could not retrieve EEPROM board information" >&2
	exit 1
    fi
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

[ -f ${cvm_bin} ] && rm -f ${cvm_bin}

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
[ -n "$BOARDSKU" ] || BOARDSKU=1000
[ -n "$FAB" ] || FAB=B02
[ -n "$fuselevel" ] || fuselevel=fuselevel_production
[ -n "${BOOTDEV}" ] || BOOTDEV="mmcblk0p1"

rm -f ${MACHINE}_bootblob_ver.txt
echo "NV3" >${MACHINE}_bootblob_ver.txt
. bsp_version
echo "# R$BSP_BRANCH , REVISION: $BSP_MAJOR.$BSP_MINOR" >>${MACHINE}_bootblob_ver.txt
echo "BOARDID=$BOARDID BOARDSKU=$BOARDSKU FAB=$FAB" >>${MACHINE}_bootblob_ver.txt
date "+%Y%m%d%H%M%S" >>${MACHINE}_bootblob_ver.txt
bytes=`cksum ${MACHINE}_bootblob_ver.txt | cut -d' ' -f2`
cksum=`cksum ${MACHINE}_bootblob_ver.txt | cut -d' ' -f1`
echo "BYTES:$bytes CRC32:$cksum" >>${MACHINE}_bootblob_ver.txt
appfile_sed=
if [ $bup_blob -ne 0 ]; then
    appfile_sed="-e/APPFILE/d -e/DATAFILE/d"
elif [ $no_flash -eq 0 ]; then
    if [ -n "$imgfile" -a -e "$imgfile" ]; then
	appfile_sed="-es,APPFILE,$imgfile, -es,DATAFILE,$dataimg,"
    else
	echo "ERR: rootfs image not specified or missing: $imgfile" >&2
	exit 1
    fi
else
    touch APPFILE
    [ -z "$dataimg" ] || touch DATAFILE
fi
sed -e"s,VERFILE,${MACHINE}_bootblob_ver.txt," -e"s,BPFDTB-FILE,$BPFDTB_FILE," $appfile_sed "$flash_in" > flash.xml

BINSARGS="mb2_bootloader nvtboot_recovery.bin; \
mts_preboot preboot_d15_prod_cr.bin; \
mts_bootpack mce_mts_d15_prod_cr.bin; \
bpmp_fw bpmp.bin; \
bpmp_fw_dtb $BPFDTB_FILE; \
tlk tos-trusty.img; \
eks eks.img; \
bootloader_dtb $dtb_file"

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
skipuid=""
if [ -n "$keyfile" ]; then
    CHIPID="0x18"
    tegraid="$CHIPID"
    localcfgfile="flash.xml"
    dtbfilename="$dtb_file"
    tbcdtbfilename="$dtb_file"
    bpfdtbfilename="$BPFDTB_FILE"
    localbootfile="$kernfile"
    BINSARGS="--bins \"$BINSARGS\""
    flashername=nvtboot_recovery_cpu.bin
    BCT="--sdram_config"
    bctfilename="$sdramcfg_file"
    SOSARGS="--applet mb1_recovery_prod.bin "
    BCTARGS="$bctargs"
    . "$here/odmsign.func"
    (odmsign_ext) || exit 1
    if [ $no_flash -ne 0 ]; then
	if [ -f flashcmd.txt ]; then
	    chmod +x flashcmd.txt
	    ln -sf flashcmd.txt ./secureflash.sh
	else
	    echo "WARN: signing completed successfully, but flashcmd.txt missing" >&2
	fi
	rm -f APPFILE DATAFILE
    fi
    [ $bup_blob -ne 0 ] || exit 0
    touch odmsign.func
fi

if [ $bup_blob -ne 0 ]; then
    tfcmd=sign
    skipuid="--skipuid"
else
    tfcmd=${flash_cmd:-"flash;reboot"}
fi

flashcmd="python3 $flashappname ${inst_args} --chip 0x18 --bl nvtboot_recovery_cpu.bin \
	      $cfgappargs \
	      --odmdata $odmdata \
	      --cmd \"$tfcmd\" $skipuid \
	      --cfg flash.xml \
	      $bctargs \
	      --bins \"$BINSARGS\""

if [ $bup_blob -ne 0 ]; then
    [ -z "$keyfile" ] || flashcmd="${flashcmd} --key \"$keyfile\""
    [ -z "$sbk_keyfile" ] || flashcmd="${flashcmd} --encrypt_key \"$sbk_keyfile\""
    support_multi_spec=1
    clean_up=0
    dtbfilename="$dtb_file"
    tbcdtbfilename="$dtb_file"
    bpfdtbfilename="$BPFDTB_FILE"
    localbootfile="boot.img"
    . "$here/l4t_bup_gen.func"
    spec="${BOARDID}-${FAB}-${BOARDSKU}--1-0-${MACHINE}-${BOOTDEV}"
    l4t_bup_gen "$flashcmd" "$spec" "$fuselevel" t186ref "$keyfile" "$sbk_keyfile" 0x18 || exit 1
else
    eval $flashcmd || exit 1
fi
