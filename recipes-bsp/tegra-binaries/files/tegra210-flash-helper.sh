#!/bin/bash
flash_in="$1"
dtb_file="$2"
sdramcfg_file="$3"
odmdata="$4"
boardcfg_file="$5"

here=$(readlink -f $(dirname "$0"))
flashapp=$here/tegraflash.py
BR_CID=$($here/tegrarcm --uid | grep BR_CID | cut -d' ' -f2)
if [ -n "$BR_CID" ]; then
    chipid="0x${BR_CID:3:2}"
    if [ "$chipid" = "0x00" ]; then
	chipid="0x${BR_CID:6:2}"
    fi
fi
if [ "$chipid" != "0x21" ]; then
    echo "ERR: chip does not identify as tegra210 ($chipid)" >&2
    exit 1
fi
if python $flashapp --chip 0x21 --skipuid --applet nvtboot_recovery.bin --cmd "dump eeprom boardinfo cvm.bin"; then
    boardid=`$here/chkbdinfo -i cvm.bin | tr -d ' ' | tr [a-z] [A-Z]`
    boardver=`$here/chkbdinfo -f cvm.bin | tr -d ' ' | tr [a-z] [A-Z]`
    boardsku=`$here/chkbdinfo -k cvm.bin | tr -d ' ' | tr [a-z] [A-Z]`
    boardrev=`$here/chkbdinfo -r cvm.bin | tr -d ' ' | tr [a-z] [A-Z]`
else
    echo "ERR: could not retrieve EEPROM board information" >&2
    exit 1
fi
echo "Board ID($boardid) version($boardver) SKU($boardsku) revision($boardrev)"
rm -f verfile.txt
echo "NV2" >verfile.txt
cat nv_tegra_release >>verfile.txt
echo "BOARDID=$boardid BOARDSKU=$boardsku FAB=$boardver" >>verfile.txt
sed -e"s,VERFILE,verfile.txt," "$flash_in" > flash.xml
boardcfg=
[ -z "$boardcfg_file" ] || boardcfg="--boardconfig $boardcfg_file"
python $here/tegraflash.py --bl cboot.bin --bct "$sdramcfg_file" --odmdata $odmdata \
       --bldtb "$dtb_file" --applet nvtboot_recovery.bin \
       $boardcfg --cfg flash.xml --chip 0x21 \
       --cmd "flash;reboot"

