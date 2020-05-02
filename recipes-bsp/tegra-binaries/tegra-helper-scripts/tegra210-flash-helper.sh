#!/bin/bash
bup_build=
keyfile=
no_flash=0
sdcard=
make_sdcard_args=
imgfile=
blocksize=4096
ARGS=$(getopt -n $(basename "$0") -l "bup,no-flash,sdcard" -o "u:s:b:B:y" -- "$@")
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
	--sdcard)
	    sdcard=yes
	    shift
	    ;;
	-u)
	    keyfile="$2"
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
    if python $flashapp --chip 0x21 --skipuid --applet nvtboot_recovery.bin --cmd "dump eeprom boardinfo ${cvm_bin}"; then
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
    appfile=$(echo $(basename "$imgfile") | cut -d. -f1).img
else
    appfile="$imgfile"
fi
appfile_sed=
if [ -n "$bup_build" ]; then
    appfile_sed="-e/APPFILE/d"
elif [ $no_flash -eq 0 ]; then
    appfile_sed="-es,APPFILE,$appfile,"
else
    touch APPFILE
fi
sed -e"s,VERFILE,${MACHINE}_bootblob_ver.txt," -e"s,DTBFILE,$DTBFILE," $appfile_sed "$flash_in" > flash.xml
boardcfg=
[ -z "$boardcfg_file" ] || boardcfg="--boardconfig $boardcfg_file"
if [ "$bup_build" = "yes" -o "$sdcard" = "yes" ]; then
    cmd="sign"
    binargs=
else
    if [ -z "$sdcard" -a $no_flash -eq 0 ]; then
	rm -f "$appfile"
	$here/mksparse -b ${blocksize} -v --fillpattern=0 "$imgfile"  "$appfile" || exit 1
    fi
    if [ -n "$keyfile" ]; then
	CHIPID="0x21"
	tegraid="$CHIPID"
	localcfgfile="flash.xml"
	BINSARGS=
	dtbfilename="$dtb_file"
	localbootfile="$kernfile"
	flashername="cboot.bin"
	BCT="--bct"
	bctfilename="$sdramcfg_file"
	. "$here/odmsign.func"
	(odmsign_ext) || exit 1
	if [ $no_flash -ne 0 ]; then
	    rm -f flashcmd.txt
	    echo "#!/bin/sh" > flashcmd.txt
	    if [ "$boardid" = "3448" ]; then
		binargs="--bins \"EBT cboot.bin.signed;DTB ${dtb_file}.signed\""
	    fi
	    echo "python $flashapp --bl cboot.bin.signed --bct \"$sdramcfg_file\" --odmdata $odmdata \
--bldtb \"${dtb_file}.signed\" --applet rcm_1_signed.rcm --cfg flash.xml --chip 0x21 \
--cmd \"secureflash;reboot\" $binargs" > flashcmd.txt
	    chmod +x flashcmd.txt
	    ln -sf flashcmd.txt ./secureflash.sh
	    rm APPFILE
	fi
	exit 0
    else
	cmd="flash;reboot"
	if [ "$boardid" = "3448" ]; then
	    binargs="--bins \"EBT cboot.bin;DTB $dtb_file\""
	fi
    fi
fi

flashcmd="python $flashapp --bl cboot.bin --bct \"$sdramcfg_file\" --odmdata $odmdata \
 --bldtb \"$dtb_file\" --applet nvtboot_recovery.bin \
 $boardcfg --cfg flash.xml --chip 0x21 --cmd \"$cmd\" $binargs"

if [ "$bup_build" = "yes" ]; then
    [ -z "$keyfile" ] || flashcmd="${flashcmd} --key \"$keyfile\""
    support_multi_spec=1
    clean_up=0
    dtbfilename="$dtb_file"
    tbcdtbfilename="$dtb_file"
    bpfdtbfilename="$BPFDTB_FILE"
    localbootfile="boot.img"
    . "$here/l4t_bup_gen.func"
    spec="${BOARDID}-${FAB}-${BOARDSKU}-${BOARDREV}-1-0-${MACHINE}-${BOOTDEV}"
    l4t_bup_gen "$flashcmd" "$spec" "$fuselevel" t210ref "$keyfile" 0x21 || exit 1
else
    eval "$flashcmd" || exit 1
    if [ -n "$sdcard" ]; then
	$here/make-sdcard $make_sdcard_args signed/flash.xml "$@"
    fi
fi
