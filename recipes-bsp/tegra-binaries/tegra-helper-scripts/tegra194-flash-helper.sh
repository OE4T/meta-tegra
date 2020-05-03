#!/bin/bash
bup_build=
keyfile=
sbk_keyfile=
no_flash=0
flash_cmd=
imgfile=

ARGS=$(getopt -n $(basename "$0") -l "bup,no-flash" -o "u:v:" -- "$@")
if [ $? -ne 0 ]; then
    echo "Error parsing options" >&2
    exit 1
fi
eval set -- "$ARGS"
unset ARGS

while true; do
    case "$1" in
	--bup)
	    bup_build=yes
	    shift
	    ;;
	--no-flash)
	    no_flash=1
	    shift
	    ;;
	-u)
	    keyfile="$2"
	    shift 2
	    ;;
	-v)
	    sbk_keyfile="$2"
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
if [ -z "$CHIPREV" ]; then
    chipid=`$here/tegrarcm_v2 --uid | grep BR_CID | cut -d' ' -f2`
    if [ -z "$chipid" ]; then
	echo "ERR: could not retrieve chip ID" >&2
	exit 1
    fi
    if [ "${chipid:3:2}" != "80" -o "${chipid:6:2}" != "19" ]; then
	echo "ERR: chip ID mismatch for Xavier" >&2
	exit 1
    fi
    if [ "${chipid:2:1}" != "8" ]; then
	echo "ERR: non-production chip found" >&2
	exit 1
    fi
    CHIPREV="${chipid:5:1}"
    skipuid="--skipuid"
fi

if [ -z "$FAB" -o -z "$BOARDID" -o \( "$BOARDID" = "2888" -a \( -z "$BOARDSKU" -o \( "$BOARDSKU" != "0004" -a -z "$BOARDREV" \) \) \) ]; then
    if ! python $flashappname --chip 0x19 --applet mb1_t194_prod.bin $skipuid --soft_fuses tegra194-mb1-soft-fuses-l4t.cfg \
		 --bins "mb2_applet nvtboot_applet_t194.bin" --cmd "dump eeprom boardinfo ${cvm_bin};reboot recovery"; then
	echo "ERR: could not retrieve EEPROM board information" >&2
	exit 1
    fi
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
else
    board_sku=`$here/chkbdinfo -k ${cvm_bin} | tr -d '[:space:]' | tr [a-z] [A-Z]`
    BOARDSKU="$board_sku"
fi
if [ "${BOARDREV+isset}" = "isset" ]; then
    board_revision="$BOARDREV"
else
    board_revision=`$here/chkbdinfo -r ${cvm_bin} | tr -d '[:space:]' | tr [a-z] [A-Z]`
    BOARDREV="$board_revision"
fi

[ -f ${cvm_bin} ] && rm -f ${cvm_bin}

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
		if [ "$board_sku" = "0006" ]; then
		    BPFDTBREV="0006-a04"
		elif [ "$board_sku" = "0004" ] || [ $board_version -gt 300 -a `expr "$board_revision" \> "D.0"` -eq 1 ]; then
		    PMICREV="a04-E-0"
		    BPFDTBREV="a04"
		fi
		;;
	    *)
		echo "ERR: unrecognized board version $board_version" >&2
		exit 1
		;;
	esac
	;;
    3660)
	case $board_version in
	    [01][0-9][0-9])
		TOREV="a02"
		PMICREV="a02"
		;;
	    *)
		echo "ERR: unrecognized board version $board_version" >&2
		exit 1
		;;
	esac
	;;
    3668)
	# No revision-specific settings
	;;
    *)
	echo "ERR: unrecognized board ID $boardid" >&2
	exit 1
	;;
esac

for var in $FLASHVARS; do
    eval pat=$`echo $var`
    if [ -z "${pat+definedmaybeempty}" ]; then
	echo "ERR: missing variable: $var" >&2
	exit 1
    elif [ -n "$pat" ]; then
	eval $var=`echo $pat | sed -e"s,@BPFDTBREV@,$BPFDTBREV," -e"s,@BOARDREV@,$TOREV," -e"s,@PMICREV@,$PMICREV," -e"s,@CHIPREV@,$CHIPREV,"`
    fi
done

[ -n "$BOARDID" ] || BOARDID=2888
[ -n "$FAB" ] || FAB=400
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
if [ "$bup_build" = "yes" ]; then
    appfile_sed="-e/APPFILE/d"
elif [ $no_flash -eq 0 ]; then
    if [ -n "$imgfile" -a -e "$imgfile" ]; then
	appfile_sed="-es,APPFILE,$imgfile,"
    else
	echo "ERR: rootfs image not specified or missing: $imgfile" >&2
	exit 1
    fi
else
    touch APPFILE
fi
sed -e"s,VERFILE,${MACHINE}_bootblob_ver.txt," -e"s,BPFDTB_FILE,$BPFDTB_FILE," $appfile_sed "$flash_in" > flash.xml

BINSARGS="mb2_bootloader nvtboot_recovery_t194.bin; \
mts_preboot preboot_c10_prod_cr.bin; \
mts_mce mce_c10_prod_cr.bin; \
mts_proper mts_c10_prod_cr.bin; \
bpmp_fw bpmp_t194.bin; \
bpmp_fw_dtb $BPFDTB_FILE; \
spe_fw spe_t194.bin; \
tlk tos-trusty_t194.img; \
eks eks.img; \
bootloader_dtb $dtb_file"

if [ -n "$UPHY_CONFIG" ]; then
    bctargs="--uphy_config $UPHY_CONFIG"
else
    bctargs=
fi

bctargs="$bctargs \
	      --device_config $DEVICE_CONFIG \
	      --misc_config tegra194-mb1-bct-misc-flash.cfg \
	      --misc_cold_boot_config $MISC_COLD_BOOT_CONFIG \
	      --pinmux_config $PINMUX_CONFIG \
	      --gpioint_config $GPIOINT_CONFIG \
	      --pmic_config $PMIC_CONFIG \
	      --pmc_config $PMC_CONFIG \
	      --prod_config $PROD_CONFIG \
	      --scr_config $SCR_CONFIG \
	      --scr_cold_boot_config $SCR_COLD_BOOT_CONFIG \
	      --br_cmd_config $BR_CMD_CONFIG \
	      --dev_params $DEV_PARAMS"

if [ "$bup_build" = "yes" ]; then
    tfcmd=sign
    skipuid="--skipuid"
elif [ -n "$keyfile" ]; then
    CHIPID="0x19"
    tegraid="$CHIPID"
    localcfgfile="flash.xml"
    dtbfilename="$dtb_file"
    tbcdtbfilename="$dtb_file"
    bpfdtbfilename="$BPFDTB_FILE"
    localbootfile="$kernfile"
    BINSARGS="--bins \"$BINSARGS\""
    flashername=nvtboot_recovery_cpu_t194.bin
    BCT="--sdram_config"
    bctfilename=`echo $sdramcfg_files | cut -d, -f1`
    bctfile1name=`echo $sdramcfg_files | cut -d, -f2`
    SOSARGS="--applet mb1_t194_prod.bin "
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
	rm APPFILE
    fi
    exit 0
else
    tfcmd="flash;reboot"
fi

flashcmd="python $flashappname --chip 0x19 --bl nvtboot_recovery_cpu_t194.bin \
	      --sdram_config $sdramcfg_files \
	      --odmdata $odmdata \
	      --applet mb1_t194_prod.bin \
	      --soft_fuses tegra194-mb1-soft-fuses-l4t.cfg \
	      --cmd \"$tfcmd\" $skipuid \
	      --cfg flash.xml \
	      $bctargs \
	      --bins \"$BINSARGS\""

if [ "$bup_build" = "yes" ]; then
    [ -z "$keyfile" ] || flashcmd="${flashcmd} --key \"$keyfile\""
    [ -z "$sbk_keyfile" ] || flashcmd="${flashcmd} --encrypt_key \"$sbk_keyfile\""
    support_multi_spec=1
    clean_up=0
    dtbfilename="$dtb_file"
    tbcdtbfilename="$dtb_file"
    bpfdtbfilename="$BPFDTB_FILE"
    localbootfile="boot.img"
    . "$here/l4t_bup_gen.func"
    spec="${BOARDID}-${FAB}-${BOARDSKU}-${BOARDREV}-1-${CHIPREV}-${MACHINE}-${BOOTDEV}"
    l4t_bup_gen "$flashcmd" "$spec" "$fuselevel" t186ref "$keyfile" 0x19 || exit 1
else
    eval $flashcmd || exit 1
fi
