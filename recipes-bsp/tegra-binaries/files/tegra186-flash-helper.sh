#!/bin/bash
bup_build=
fuse_burn=
secureflash=
if [ "$1" = "--bup" ]; then
    bup_build=yes
    shift
elif [ "$1" = "--burnfuses" ]; then
    fuse_burn=yes
    shift
elif [ "$1" = "--secureflash" ]; then
    secureflash=yes
    shift
fi
flash_in="$1"
dtb_file="$2"
sdramcfg_file="$3"
odmdata="$4"

here=$(readlink -f $(dirname "$0"))
flashapp=$(which tegraflash.py)

if [ ! -e ./flashvars ]; then
    echo "ERR: missing flash variables file" >&2
    exit 1
fi

. ./flashvars

if [ -z "$FLASHVARS" ]; then
    echo "ERR: flash variable set not defined" >&2
    exit 1
fi

# The following defaults are for the B00 revision SOM
# which shipped with at least some Jetson TX2 dev kits.
# BOARDREV is used for all substitutions, except for
# BPFDTB and PMIC revisions, which differ between B00
# and B01 revisions.  See p2771-0000.conf.common in
# the L4T kit.
BOARDREV="c03"
BPFDTBREV="c01"
PMICREV="c03"

if [ -n "$FAB" ]; then
    boardrev="$FAB"
elif python "$flashapp" --chip 0x18 --applet mb1_recovery_prod.bin --cmd "dump eeprom boardinfo cvm.bin"; then
    boardrev=`chkbdinfo -f cvm.bin`
    boardrev=`echo $boardrev | tr [a-z] [A-Z]`
else
    echo "ERR: could not retrieve EEPROM board information" >&2
    exit 1
fi
case $boardrev in
    B0[1-9]|[C-Z]??)
	BPFDTBREV="c04"
	PMICREV="c04"
	;;
    B00)
	;;
    *)
	echo "ERR: unsupported board revision: $boardrev" >&2
	exit 1
	;;
esac

for var in $FLASHVARS; do
    eval pat=$`echo $var`
    if [ -z "$pat" ]; then
	echo "ERR: missing variable: $var" >&2
	exit 1
    fi
    eval $var=`echo $pat | sed -e"s,@BPFDTBREV@,$BPFDTBREV," -e"s,@BOARDREV@,$BOARDREV," -e"s,@PMICREV@,$PMICREV,"`
done

[ -n "$BOARDID" ] || BOARDID=3310
[ -n "$FAB" ] || FAB=B02
[ -n "$fuselevel" ] || fuselevel=fuselevel_production
spec="${BOARDID}-${FAB}-${fuselevel}"

sed -e"s,BPFDTB-FILE,$BPFDTB_FILE," "$flash_in" > flash.xml

BINS="mb2_bootloader nvtboot_recovery.bin; \
mts_preboot preboot_d15_prod_cr.bin; \
mts_bootpack mce_mts_d15_prod_cr.bin; \
bpmp_fw bpmp.bin; \
bpmp_fw_dtb $BPFDTB_FILE; \
tlk tos-mon-only.img; \
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
flashcfg="--cfg flash.xml"
blarg="--bl nvtboot_recovery_cpu.bin"
skipuid=""
if [ "$bup_build" = "yes" ]; then
    tfcmd=sign
    skipuid="--skipuid"
elif [ "$fuse_burn" = "yes" ]; then
    tfcmd="burnfuses odmfuse_pkc.xml"
elif [ "$secureflash" = "yes" ]; then
    tfcmd="secureflash;reboot"
    cfgappargs="--bct br_bct_BR.bct \
              --applet rcm_1_signed.rcm"
    bctargs="--mb1_bct mb1_bct_MB1_sigheader.bct.signed"
    flashcfg="--cfg secureflash.xml"
    blarg="--bl nvtboot_recovery_cpu_sigheader.bin.signed"
    BINS="mb2_bootloader nvtboot_recovery_sigheader.bin.signed; \
mts_preboot preboot_d15_prod_cr_sigheader.bin.signed; \
mts_bootpack mce_mts_d15_prod_cr_sigheader.bin.signed; \
bpmp_fw bpmp_sigheader.bin.signed; \
bpmp_fw_dtb `basename $BPFDTB_FILE .dtb`_sigheader.dtb.signed; \
tlk tos_sigheader.img.signed; \
eks eks_sigheader.img.signed; \
bootloader_dtb `basename $dtb_file .dtb`_sigheader.dtb.signed"
else
    tfcmd="flash;reboot"
fi
flashcmd="python $flashapp --chip 0x18 $blarg \
	      $cfgappargs \
	      --odmdata $odmdata \
	      --cmd \"$tfcmd\" $skipuid \
	      $flashcfg \
	      $bctargs \
	      --bins \"$BINS\""

if [ "$bup_build" = "yes" ]; then
    support_multi_spec=0
    clean_up=0
    dtbfilename="$dtb_file"
    tbcdtbfilename="$dtb_file"
    bpfdtbfilename="$BPFDTB_FILE"
    localbootfile="boot.img"
    . "$here/l4t_bup_gen.func"
    l4t_bup_gen "$flashcmd" "$spec" "$fuselevel" t186ref "" 0x18 || exit 1
else
    eval $flashcmd || exit 1
fi
