#!/bin/bash
bup_build=
keyfile=
sbk_keyfile=
no_flash=0
flash_cmd=

ARGS=$(getopt -n $(basename "$0") -l "bup,no-flash" -o "u:v:" -- "$@")
if [ $? -ne 0 ]; then
    echo "Error parsing options" >&2
    exit 1
fi
eval set -- "$ARGS"

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

if [ -z "$FAB" -o -z "$BOARDID" -o -z "$BOARDSKU" -o -z "$BOARDREV" ]; then
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
fi
if [ -n "$FAB" ]; then
    board_version="$FAB"
else
    board_version=`$here/chkbdinfo -f ${cvm_bin} | tr -d '[:space:]' | tr [a-z] [A-Z]`
fi
if [ -n "$BOARDSKU" ]; then
    board_sku="$BOARDSKU"
else
    board_sku=`$here/chkbdinfo -k ${cvm_bin} | tr -d '[:space:]' | tr [a-z] [A-Z]`
fi
if [ -n "$BOARDREV" ]; then
    board_revision="$BOARDREV"
else
    board_revision=`$here/chkbdinfo -r ${cvm_bin} | tr -d '[:space:]' | tr [a-z] [A-Z]`
fi

[ -f ${cvm_bin} ] && rm -f ${cvm_bin}

# Adapted from p2972-0000.conf.common in L4T kit
TOREV="a01"
BPFDTBREV="a01"
PMICREV="a01"

if [ "$boardid" = "2888" ]; then
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
	    if [ "$board_sku" = "0004" ] || [ $board_version -gt 300 -a `expr "$board_revision" \> "D.0"` -eq 1 ]; then
		PMICREV="a04-E-0"
		BPFDTBREV="a04"
	    fi
	    ;;
	*)
	    echo "ERR: unrecognized board version $board_version" >&2
	    exit 1
	    ;;
    esac
elif [ "$BOARDID" = "3660" ]; then
    case $board_version in
	[01][0-9][0-9])
	    TOREV="a02"
	    PMICREV="a02"
	    ;;
	*)
	    echo "ERR: unrecognized board revision $boardrev" >&2
	    exit 1
	    ;;
    esac
else
    echo "ERR: unrecognized board ID $BOARDID" >&2
    exit 1
fi

for var in $FLASHVARS; do
    eval pat=$`echo $var`
    if [ -z "$pat" ]; then
	echo "ERR: missing variable: $var" >&2
	exit 1
    fi
    eval $var=`echo $pat | sed -e"s,@BPFDTBREV@,$BPFDTBREV," -e"s,@BOARDREV@,$TOREV," -e"s,@PMICREV@,$PMICREV," -e"s,@CHIPREV@,$CHIPREV,"`
done

[ -n "$BOARDID" ] || BOARDID=2888
[ -n "$FAB" ] || FAB=400
[ -n "$fuselevel" ] || fuselevel=fuselevel_production
spec="${BOARDID}-${FAB}-${BOARDSKU}-${BOARDREV}-1-${CHIPREV}-${MACHINE}"

sed -e"s,BPFDTB_FILE,$BPFDTB_FILE," "$flash_in" > flash.xml

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

bctargs="--uphy_config tegra194-mb1-uphy-lane-p2888-0000-p2822-0000.cfg \
	      --device_config tegra19x-mb1-bct-device-sdmmc.cfg \
	      --misc_config tegra194-mb1-bct-misc-flash.cfg \
	      --misc_cold_boot_config tegra194-mb1-bct-misc-l4t.cfg \
	      --pinmux_config tegra19x-mb1-pinmux-p2888-0000-a04-p2822-0000-b01.cfg \
	      --gpioint_config tegra194-mb1-bct-gpioint-p2888-0000-p2822-0000.cfg \
	      --pmic_config $PMIC_CONFIG \
	      --pmc_config tegra19x-mb1-padvoltage-p2888-0000-a00-p2822-0000-a00.cfg \
	      --prod_config tegra19x-mb1-prod-p2888-0000-p2822-0000.cfg \
	      --scr_config tegra194-mb1-bct-scr-cbb-mini.cfg \
	      --scr_cold_boot_config tegra194-mb1-bct-scr-cbb-mini.cfg \
	      --br_cmd_config tegra194-mb1-bct-reset-p2888-0000-p2822-0000.cfg \
	      --dev_params tegra194-br-bct-sdmmc.cfg"

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
    flashername=nvtboot_recovery_cpu.bin
    BCT="--sdram_config"
    bctfilename=`echo $sdramcfg_files | cut -d, -f1`
    bctfile1name=`echo $sdramcfg_files | cut -d, -f2`
    SOSARGS="--applet mb1_t194_prod.bin "
    BCTARGS="$bctargs"
    . "$here/odmsign.func"
    odmsign_ext || exit 1
    if [ $no_flash -ne 0 ]; then
	if [ -f flashcmd.txt ]; then
	    chmod +x flashcmd.txt
	    ln -sf flashcmd.txt ./secureflash.sh
	else
	    echo "WARN: signing completed successfully, but flashcmd.txt missing" >&2
	fi
    fi
    exit 0
else
    tfcmd=${flash_cmd:-"flash;reboot"}
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
    support_multi_spec=0
    clean_up=0
    dtbfilename="$dtb_file"
    tbcdtbfilename="$dtb_file"
    bpfdtbfilename="$BPFDTB_FILE"
    localbootfile="boot.img"
    . "$here/l4t_bup_gen.func"
    l4t_bup_gen "$flashcmd" "$spec" "$fuselevel" t186ref "$keyfile" 0x19 || exit 1
else
    eval $flashcmd || exit 1
fi
