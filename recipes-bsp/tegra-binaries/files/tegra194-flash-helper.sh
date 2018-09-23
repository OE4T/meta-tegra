#!/bin/bash
flash_in="$1"
dtb_file="$2"
sdramcfg_files="$3"
odmdata="$4"

BPFDTB_FILE="tegra194-a0@CHIPREV@-bpmp-p2888-@BPFDTBREV@.dtb"
PMIC_CONFIG="tegra194-mb1-bct-pmic-p2888-0001-@REV@-p2822-0000.cfg"

BOARDREV="a01"
BPFDTBREV="a01"
PMICREV="a01"

chipid=`tegrarcm_v2 --uid | grep BR_CID | cut -d' ' -f2`
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

if tegraflash.py --chip 0x19 --applet mb1_t194_prod.bin --skipuid --soft_fuses tegra194-mb1-soft-fuses-l4t.cfg \
		 --bins "mb2_applet nvtboot_applet_t194.bin" --cmd "dump eeprom boardinfo cvm.bin;reboot recovery"; then
    boardid=`chkbdinfo -i cvm.bin | tr -d ' '`
    boardrev=`chkbdinfo -f cvm.bin | tr -d ' '`
    boardrev=`echo $boardrev | tr [a-z] [A-Z]`
    if [ "$boardid" = "2888" ]; then
	case $boardrev in
	    [01][0-9][0-9])
		;;
	    2[0-9][0-9])
		BOARDREV="a02"
		PMICREV="a02"
		BPFDTBREV="a02"
		;;
	    [34][0-9][0-9])
		BOARDREV="a02"
		PMICREV="a04"
		BPFDTBREV="a04"
		;;
	    *)
		echo "ERR: unrecognized board revision $boardrev" >&2
		exit 1
		;;
	esac
    elif [ "$boardid" = "3660" ]; then
	case $boardrev in
	    [01][0-9][0-9])
		BOARDREV="a02"
		PMICREV="a02"
		;;
	    *)
		echo "ERR: unrecognized board revision $boardrev" >&2
		exit 1
		;;
	esac
    else
	echo "ERR: unrecognized board ID $boardid" >&2
	exit 1
    fi
else
    echo "ERR: could not retrieve EEPROM board information" >&2
    exit 1
fi

BPFDTB_FILE=`echo $BPFDTB_FILE | sed -e"s,@CHIPREV@,$CHIPREV," -e"s,@BPFDTBREV@,$BPFDTBREV,"`
sed -e"s,BPFDTB_FILE,$BPFDTB_FILE," \
    "$flash_in" > flash.xml

PMIC_CONFIG=`echo $PMIC_CONFIG | sed -e"s,@REV@,$PMICREV,"`

BINS="mb2_bootloader nvtboot_recovery_t194.bin; \
mts_preboot preboot_c10_prod_cr.bin; \
mts_mce mce_c10_prod_cr.bin; \
mts_proper mts_c10_prod_cr.bin; \
bpmp_fw bpmp_t194.bin; \
bpmp_fw_dtb $BPFDTB_FILE; \
spe_fw spe_t194.bin; \
tlk tos_t194.img; \
eks eks.img; \
bootloader_dtb $dtb_file"

tegraflash.py --chip 0x19 --bl nvtboot_recovery_cpu_t194.bin \
	      --sdram_config $sdramcfg_files \
	      --odmdata $odmdata \
	      --applet mb1_t194_prod.bin \
	      --soft_fuses tegra194-mb1-soft-fuses-l4t.cfg \
	      --cmd "flash;reboot" \
	      --cfg flash.xml \
	      --uphy_config tegra194-mb1-uphy-lane-p2888-0000-p2822-0000.cfg \
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
	      --dev_params tegra194-br-bct-sdmmc.cfg \
	      --bins "$BINS" || exit 1
