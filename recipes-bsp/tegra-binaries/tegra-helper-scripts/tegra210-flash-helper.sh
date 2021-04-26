#!/bin/bash
bup_blob=0
keyfile=
keyfile_args=
spi_only=
no_flash=0
flash_cmd=
sdcard=
make_sdcard_args=
imgfile=
dataimg=
inst_args=""
blocksize=4096

ARGS=$(getopt -n $(basename "$0") -l "bup,no-flash,sdcard,spi-only,datafile:,usb-instance:" -o "u:s:b:B:yc:" -- "$@")
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
	--sdcard)
	    sdcard=yes
	    shift
	    ;;
	--spi-only)
	    spi_only=yes
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
	    keyfile_args="--key \"$keyfile\""
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
sdramcfg_file="$3"
odmdata="$4"
boardcfg_file="$5"
kernfile="$6"
imgfile="$7"
shift 7

here=$(readlink -f $(dirname "$0"))
flashapp=$here/tegraflash.py

if [ -e ./flashvars ]; then
    . ./flashvars
fi

# Temp file for storing cvm.bin in, if we need to query the board for its
# attributes
cvm_bin=$(mktemp cvm.bin.XXXXX)

if [ -z "$BOARDID" -a -z "$FAB" ]; then
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
    if python3 $flashapp ${inst_args} --chip 0x21 --skipuid $keyfile_args --applet nvtboot_recovery.bin --cmd "dump eeprom boardinfo ${cvm_bin}"; then
	boardid=`$here/chkbdinfo -i ${cvm_bin} | tr -d ' ' | tr [a-z] [A-Z]`
	BOARDID="$boardid"
	boardver=`$here/chkbdinfo -f ${cvm_bin} | tr -d ' ' | tr [a-z] [A-Z]`
	FAB="$boardver"
	boardsku=`$here/chkbdinfo -k ${cvm_bin} | tr -d ' ' | tr [a-z] [A-Z]`
	BOARDSKU="$boardsku"
	boardrev=`$here/chkbdinfo -r ${cvm_bin} | tr -d ' ' | tr [a-z] [A-Z]`
	BOARDREV="$boardrev"
    else
	echo "ERR: could not retrieve EEPROM board information" >&2
	exit 1
    fi
    echo "Board ID($boardid) version($boardver) SKU($boardsku) revision($boardrev)"
else
    boardid=$BOARDID
    boardver=$FAB
    boardsku=${BOARDSKU}
    boardrev=${BOARDREV}
    echo "Board ID($boardid) version($boardver) SKU($boardsku) revision($boardrev) from environment"
fi

if [ "$boardid" = "3448" ]; then
    if expr "$boardver" \< "300" >/dev/null 2>&1; then
	dtbfab="a02"
    else
	dtbfab="b00"
    fi
    if [ -z "$boardsku" ]; then
	boardsku="0000"
	BOARDSKU="0000"
    fi
    for var in $FLASHVARS; do
	eval pat=$`echo $var`
	if [ -z "$pat" ]; then
	    echo "ERR: missing variable: $var" >&2
	    exit 1
	fi
	eval $var=`echo $pat | sed -e"s,@BOARDSKU@,$BOARDSKU," -e"s,@DTBFAB@,$dtbfab,"`
    done
fi

if [ -n "$DTBFILE" ]; then
    dtb_file="$DTBFILE"
else
    DTBFILE="$dtb_file"
fi

[ -f ${cvm_bin} ] && rm -f ${cvm_bin}

[ -n "$fuselevel" ] || fuselevel=fuselevel_production
[ -n "${BOOTDEV}" ] || BOOTDEV="mmcblk0p1"

rm -f ${MACHINE}_bootblob_ver.txt
echo "NV3" >${MACHINE}_bootblob_ver.txt
. bsp_version
echo "# R$BSP_BRANCH , REVISION: $BSP_MAJOR.$BSP_MINOR" >>${MACHINE}_bootblob_ver.txt
echo "BOARDID=$boardid BOARDSKU=$boardsku FAB=$boardver" >>${MACHINE}_bootblob_ver.txt
date "+%Y%m%d%H%M%S" >>${MACHINE}_bootblob_ver.txt
bytes=`cksum ${MACHINE}_bootblob_ver.txt | cut -d' ' -f2`
cksum=`cksum ${MACHINE}_bootblob_ver.txt | cut -d' ' -f1`
echo "BYTES:$bytes CRC32:$cksum" >>${MACHINE}_bootblob_ver.txt
if [ -z "$sdcard" ]; then
    appfile=$(basename "$imgfile").img
    if [ -n "$dataimg" ]; then
	datafile=$(basename "$dataimg").img
    fi
else
    appfile="$imgfile"
    datafile="$dataimg"
fi
appfile_sed=
if [ $bup_blob -ne 0 ]; then
    appfile_sed="-e/APPFILE/d -e/DATAFILE/d"
elif [ $no_flash -eq 0 -a -z "$sdcard" ]; then
    appfile_sed="-es,APPFILE,$appfile, -es,DATAFILE,$datafile,"
else
    pre_sdcard_sed="-es,APPFILE,$appfile,"
    if [ -n "$datafile" ]; then
	pre_sdcard_sed="$pre_sdcard_sed -es,DATAFILE,$datafile,"
	touch DATAFILE
    fi
    touch APPFILE
fi
if [ "$spi_only" = "yes" ]; then
    if [ ! -e "$here/nvflashxmlparse" ]; then
	echo "ERR: missing nvflashxmlparse script" >&2
	exit 1
    fi
    "$here/nvflashxmlparse" --extract -t spi -o flash.xml.tmp "$flash_in" || exit 1
else
    cp "$flash_in" flash.xml.tmp
fi
sed -e"s,VERFILE,${MACHINE}_bootblob_ver.txt," -e"s,DTBFILE,$DTBFILE," $appfile_sed flash.xml.tmp > flash.xml
rm flash.xml.tmp
boardcfg=
[ -z "$boardcfg_file" ] || boardcfg="--boardconfig $boardcfg_file"
if [ $bup_blob -ne 0 -o "$sdcard" = "yes" ]; then
    cmd="sign"
    binargs=
else
    if [ -z "$sdcard" -a $no_flash -eq 0 ]; then
	rm -f "$appfile"
	$here/mksparse -b ${blocksize} --fillpattern=0 "$imgfile"  "$appfile" || exit 1
	if [ -n "$datafile" ]; then
	    rm -f "$datafile"
	    $here/mksparse -b ${blocksize} --fillpattern=0 "$dataimg" "$datafile" || exit 1
	fi
    fi
    cmd=${flash_cmd:-"flash;reboot"}
    if [ "$boardid" = "3448" ]; then
	binargs="--bins \"EBT cboot.bin;DTB $dtb_file\""
    fi
fi

if [ -n "$keyfile" ]; then
    dbmaster=$(readlink -f "$keyfile")
    fusetype="PKC"
    CHIPID="0x21"
    tegraid="$CHIPID"
    localcfgfile="flash.xml"
    BINSARGS=
    DTBARGS="--bldtb $dtb_file "
    SOSARGS=" --applet nvtboot_recovery.bin "
    dtbfilename="$dtb_file"
    localbootfile="$kernfile"
    flashername="cboot.bin"
    bootloadername="cboot.bin"
    BCT="--bct"
    bctfilename="$sdramcfg_file"
    flashappname=$(basename "$flashapp")
    . "$here/odmsign.func"
    (odmsign_ext) || exit 1
    binargs=
    if [ "$boardid" = "3448" ]; then
	binargs="--bins \"EBT cboot.bin.signed;DTB ${dtb_file}.signed\""
    fi
    if [ $no_flash -ne 0 ]; then
	rm -f flashcmd.txt
	echo "#!/bin/sh" > flashcmd.txt
	echo "python3 $flashapp ${inst_args} --bl cboot.bin.signed --bct \"$(basename $sdramcfg_file .cfg).bct\" --odmdata $odmdata \
--bldtb \"${dtb_file}.signed\" --applet rcm_1_signed.rcm --cfg flash.xml --chip 0x21 \
--cmd \"secureflash;reboot\" $binargs" > flashcmd.txt
	chmod +x flashcmd.txt
	ln -sf flashcmd.txt ./secureflash.sh
	rm -f APPFILE DATAFILE
	exit 0
    fi
    if [ $bup_blob -eq 0 -a "$sdcard" != "yes" ]; then
	flashcmd="python3 $flashapp ${inst_args} --bl cboot.bin.signed --bct \"$(basename $sdramcfg_file .cfg).bct\" --odmdata $odmdata \
--bldtb \"${dtb_file}.signed\" --applet rcm_1_signed.rcm --cfg flash.xml --chip 0x21 \
--cmd \"secureflash;reboot\" $binargs"
	eval "$flashcmd" || exit 1
	exit 0
    fi
    touch odmsign.func
fi

flashcmd="python3 $flashapp ${inst_args} --bl cboot.bin --bct \"$sdramcfg_file\" --odmdata $odmdata \
 --bldtb \"$dtb_file\" --applet nvtboot_recovery.bin \
 $boardcfg --cfg flash.xml --chip 0x21 --cmd \"$cmd\" $binargs"

if [ $bup_blob -ne 0 ]; then
    [ -z "$keyfile" ] || flashcmd="${flashcmd} $keyfile_args"
    support_multi_spec=1
    clean_up=0
    dtbfilename="$dtb_file"
    tbcdtbfilename="$dtb_file"
    bpfdtbfilename="$BPFDTB_FILE"
    localbootfile="boot.img"
    . "$here/l4t_bup_gen.func"
    spec="${BOARDID}-${FAB}-${BOARDSKU}-${BOARDREV}-1-0-${MACHINE}-${BOOTDEV}"
    l4t_bup_gen "$flashcmd" "$spec" "$fuselevel" t210ref "$keyfile" "" 0x21 || exit 1
else
    if [ -z "$keyfile" ]; then
	eval "$flashcmd" || exit 1
    fi
    if [ -n "$sdcard" ]; then
	if [ -n "$pre_sdcard_sed" ]; then
	    rm -f signed/flash.xml.in
	    mv signed/flash.xml signed/flash.xml.in
	    sed $pre_sdcard_sed  signed/flash.xml.in > signed/flash.xml
	fi
	$here/make-sdcard $make_sdcard_args signed/flash.xml "$@"
    fi
fi
