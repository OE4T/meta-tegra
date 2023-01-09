#!/bin/bash
bup_blob=0
keyfile=
keyfile_args=
spi_only=
no_flash=0
to_sign=0
external_device=0
rcm_boot=0
flash_cmd=
sdcard=
make_sdcard_args=
imgfile=
dataimg=
inst_args=""
blocksize=4096
bootloader_part="sdmmc_boot"
boardcfg_file=
ignorebfs=

generate_pt_header() {
    local in="$1"
    local out="$2"
    local blpart="$3"
    local pline blksize partnumber partname start_location partsize partfile partguid partfilltoend
    local -a PARTS
    local plist=$(mktemp partlist.XXXXXX)

    nvflashxmlparse -t $blpart "$in" >"$plist"
    mapfile PARTS < "$plist"
    for pline in "${PARTS[@]}"; do
	eval "$pline"
	if [ "$partname" = "PT" ]; then
	    break
	fi
    done
    rm -f "$plist"
    if [ "$partname" != "PT" ]; then
	echo "ERR: could not locate PT in flash layout" >&2
	return 1
    fi
    local ptsize=$(expr $partsize \* $blksize)
    rm -f crc-flash.xml.tmp "$out"
    sed -e"s,$partfile,crc-$partfile," "$in" > crc-flash.xml.tmp
    tegraparser --pt crc-flash.xml.tmp || return 1
    cp crc-flash.xml.tmp "$out"
    truncate -s $ptsize crc-$partfile
    printf "\x01\x00\x00\x00\x00\x00\x00\x00PTHD\x00\x00\x00\x00" | \
	dd of=crc-$partfile seek=$(expr $ptsize - 16) bs=1 count=16 conv=notrunc status=none || return 1
    tegrahost --fillcrc32 crc-$partfile
    return 0
}

ARGS=$(getopt -n $(basename "$0") -l "bup,no-flash,sign,external-device,rcm-boot,sdcard,spi-only,datafile:,usb-instance:" -o "u:s:b:B:yc:" -- "$@")
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
	--sdcard)
	    sdcard=yes
	    shift
	    ;;
	--spi-only)
	    spi_only=yes
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
	--usb-instance)
	    usb_instance="$2"
	    inst_args="--instance ${usb_instance}"
	    shift 2
	    ;;
	-u)
	    keyfile="$2"
	    [ -z "$keyfile" ] || keyfile_args="--key \"$keyfile\""
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
# Allow for the older syntax that had the
# board config file as an argument
if [ "${5%.xml}" != "$5" -o -z "$5" ]; then
    boardcfg_file="$5"
    kernfile="$6"
    imgfile="$7"
    shift 7
else
    kernfile="$5"
    imgfile="$6"
    shift 6
fi

here=$(readlink -f $(dirname "$0"))
PATH="$here:$PATH"
flashapp=tegraflash.py

if [ -e ./flashvars ]; then
    . ./flashvars
fi

# Temp file for storing cvm.bin in, if we need to query the board for its
# attributes
cvm_bin=$(mktemp cvm.bin.XXXXX)

BR_CID=
have_boardinfo=
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
    cvmcmd="python3 $flashapp ${inst_args} --chip 0x21 --skipuid $keyfile_args --applet nvtboot_recovery.bin --cmd \"dump eeprom boardinfo ${cvm_bin}\""
    if eval "$cvmcmd"; then
	BOARDID=$($here/chkbdinfo -i ${cvm_bin} | tr -d ' ' | tr [a-z] [A-Z])
	FAB=$($here/chkbdinfo -f ${cvm_bin} | tr -d ' ' | tr [a-z] [A-Z])
	BOARDSKU=$($here/chkbdinfo -k ${cvm_bin} | tr -d ' ' | tr [a-z] [A-Z])
	BOARDREV=$($here/chkbdinfo -r ${cvm_bin} | tr -d ' ' | tr [a-z] [A-Z])
	if [ -z "$serial_number" ]; then
	    serial_number=$($here/chkbdinfo -a ${cvm_bin} | tr -d '[:space:]')
	fi
    else
	echo "ERR: could not retrieve EEPROM board information" >&2
	exit 1
    fi
fi

rm -f boardvars.sh
cat >boardvars.sh <<EOF
BOARDID="$BOARDID"
FAB="$FAB"
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

boardid=$BOARDID
boardver=$FAB
boardsku=$BOARDSKU
boardrev=$BOARDREV
echo "Board ID($boardid) version($boardver) SKU($boardsku) revision($boardrev) from environment"

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
    if [ "$boardsku" != "0002" ]; then
	bootloader_part="spi"
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

cp $dtb_file rcm_$dtb_file
fdtput -p rcm_$dtb_file /chosen/plugin-manager/ids "${BOARDID}-${FAB}-${BOARDSKU}-${BOARDREV}-1-0-${MACHINE}-${BOOTDEV}"

if [ "$spi_only" = "yes" -a "$bootloader_part" != "spi" ]; then
    echo "ERR: --spi-only specified for eMMC platform" >&2
    exit 1
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

generate_pt_header "$flash_in" flash.xml.tmp "$bootloader_part" || exit 1

if [ "$spi_only" = "yes" ]; then
    cp flash.xml.tmp flash.xml.tmp-1
    "$here/nvflashxmlparse" --extract -t spi -o flash.xml.tmp flash.xml.tmp-1 || exit 1
    rm flash.xml.tmp-1
fi
sed -e"s,VERFILE,${MACHINE}_bootblob_ver.txt," -e"s,DTBFILE,$DTBFILE," \
    -e"s,TBCFILE,$TBCFILENAME," $appfile_sed flash.xml.tmp > flash.xml
sed -e"s,VERFILE,${MACHINE}_bootblob_ver.txt," -e"s,DTBFILE,rcm_$DTBFILE," \
    -e"s,TBCFILE,$RCM_TBCFILENAME," "-e/APPFILE/d" "-e/DATAFILE/d"  flash.xml.tmp > rcm-flash.xml
rm flash.xml.tmp
boardcfg=
[ -z "$boardcfg_file" ] || boardcfg="--boardconfig $boardcfg_file"

if [ -n "$keyfile" -a ! -e "$here/odmsign.func" ]; then
    echo "ERR: missing odmsign.func from secureboot package, signing not supported" >&2
    exit 1
fi

if [ $bup_blob -ne 0 -o $to_sign -ne 0 -o $external_device -eq 1 -o "$sdcard" = "yes" ]; then
    cmd="sign"
    binargs=
elif [ $rcm_boot -ne 0 ]; then
    cmd="rcmboot"
    binargs=
else
    if [ -z "$sdcard" -a $external_device -eq 0 -a $no_flash -eq 0 -a "$spi_only" != "yes" ]; then
	rm -f "$appfile"
	mksparse -b ${blocksize} --fillpattern=0 "$imgfile"  "$appfile" || exit 1
	if [ -n "$datafile" ]; then
	    rm -f "$datafile"
	    mksparse -b ${blocksize} --fillpattern=0 "$dataimg" "$datafile" || exit 1
	fi
    fi
    cmd=${flash_cmd:-"flash;reboot"}
    if [ "$boardid" = "3448" ]; then
	# Extra option for 'rapid' boot
	if echo "$TBCFILENAME" | grep -q "_rb"; then
	    ignorebfs="--ignorebfs"
	fi
	binargs="--bins \"EBT cboot.bin;DTB $dtb_file\""
    fi
fi

# For t210, can only invoke odmsign if there's a keyfile
# Signing for non-secured t210s happens through normal path below.
if [ -n "$keyfile" ]; then
    dbmaster=$(readlink -f "$keyfile")
    fusetype="PKC"
    CHIPID="0x21"
    tegraid="$CHIPID"
    BINSARGS=
    DTBARGS="--bldtb $dtb_file "
    SOSARGS=" --applet nvtboot_recovery.bin "
    localbootfile="$kernfile"
    flashername="cboot.bin"
    bootloadername="cboot.bin"
    BCT="--bct"
    bctfilename="$sdramcfg_file"
    flashappname=$(basename "$flashapp")
    . "$here/odmsign.func"
    # Have to sign twice here in the no_flash/to_sign case
    # Once for RCM boot and once for normal (but not for BUP generation)
    if [ $bup_blob -eq 0 ] && [ $rcm_boot -eq 1 -o $no_flash -eq 1 -o $to_sign -eq 1 ]; then
	dtbfilename="rcm_$dtb_file"
	tbcfilename=${RCM_TBCFILENAME}
	localcfgfile="rcm-flash.xml"
	SIGNARGS=
	(odmsign) || exit 1
    fi
    # If we're just RCM booting, we don't need the second signing
    if [ $rcm_boot -eq 0 ]; then
	dtbfilename="$dtb_file"
	tbcfilename=${TBCFILENAME}
	localcfgfile="flash.xml"
	SIGNARGS=
	(odmsign) || exit 1
    fi

    binargs=
    if [ "$boardid" = "3448" ]; then
	binargs="--bins \"EBT cboot.bin.signed;DTB ${dtb_file}.signed\""
	rcm_binargs="--bins \"EBT cboot.bin.signed;DTB rcm_${dtb_file}.signed\""
    fi
    if [ $bup_blob -eq 0 ]; then
	if [ $rcm_boot -eq 1 ]; then
	    if [ $no_flash -ne 0 ]; then
		runflashapp="python3 $(basename $flashapp)"
	    else
		runflashapp="python3 $flashapp"
	    fi
	    flashcmd="$runflashapp ${inst_args} --bct \"$(basename $sdramcfg_file .cfg).bct\" --odmdata $odmdata \
--bl ${RCM_TBCFILENAME}.signed --bldtb rcm_${dtb_file}.signed --applet rcm_1_signed.rcm --cfg rcm-flash.xml --chip 0x21 \
--lnx $localbootfile --tos tos-mon-only.img.signed --eks eks.img --kerneldtb rcm_${dtb_file}.signed $rcm_binargs $boardcfg --securedev \
--cmd \"rcmboot\""
	    if [ $no_flash -ne 0 ]; then
		rm -f flashcmd.txt
		echo "#!/bin/sh" >flashcmd.txt
		echo "$flashcmd" >>flashcmd.txt
		chmod +x flashcmd.txt
		ln -sf flashcmd.txt ./secureflash.sh
		rm -f APPFILE DATAFILE
		exit 0
	    else
		eval "$flashcmd" || exit 1
		exit 0
	    fi
	elif [ $no_flash -ne 0 ]; then
	    rm -f rcmbootcmd.txt
	    echo "#!/bin/sh" > rcmbootcmd.txt
	    echo "python3 $(basename $flashapp) ${inst_args} --bct \"$(basename $sdramcfg_file .cfg).bct\" --odmdata $odmdata \
--bl ${RCM_TBCFILENAME}.signed --bldtb rcm_${dtb_file}.signed --applet rcm_1_signed.rcm --cfg rcm-flash.xml --chip 0x21 \
--lnx $localbootfile --tos tos-mon-only.img.signed --eks eks.img --kerneldtb rcm_${dtb_file}.signed $rcm_binargs $boardcfg --securedev \
--cmd \"rcmboot\"" >> rcmbootcmd.txt
	    rm -f flashcmd.txt
	    echo "#!/bin/sh" > flashcmd.txt
	    echo "python3 $(basename $flashapp) ${inst_args} --bl cboot.bin.signed --bct \"$(basename $sdramcfg_file .cfg).bct\" --odmdata $odmdata \
--bldtb \"${dtb_file}.signed\" --applet rcm_1_signed.rcm --cfg secureflash.xml --chip 0x21 \
--cmd \"secureflash;reboot\" $binargs" > flashcmd.txt
	    mv flash.xml secureflash.xml
	    chmod +x flashcmd.txt
	    ln -sf flashcmd.txt ./secureflash.sh
	    rm -f APPFILE DATAFILE
	    exit 0
	fi
	if [ "$sdcard" != "yes" ]; then
	    flashcmd="python3 $flashapp ${inst_args} --bl cboot.bin.signed --bct \"$(basename $sdramcfg_file .cfg).bct\" --odmdata $odmdata \
--bldtb \"${dtb_file}.signed\" --applet rcm_1_signed.rcm --cfg flash.xml --chip 0x21 \
--cmd \"secureflash;reboot\" $binargs"
	    eval "$flashcmd" || exit 1
	    exit 0
	fi
    fi
    touch odmsign.func
fi

flashcmd="python3 $flashapp ${inst_args} --bl cboot.bin --bct \"$sdramcfg_file\" --odmdata $odmdata \
 --bldtb \"$dtb_file\" --applet nvtboot_recovery.bin \
 $boardcfg --cfg flash.xml --chip 0x21 $ignorebfs --cmd \"$cmd\" $binargs"

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
    if [ $(expr length "$spec") -ge 64 ]; then
	echo "ERR: TNSPEC must be shorter than 64 characters: $spec" >&2
	exit 1
    fi
    l4t_bup_gen "$flashcmd" "$spec" "$fuselevel" t210ref "$keyfile" "" 0x21 || exit 1
    exit 0
fi

if [ $to_sign -ne 0 -o $rcm_boot -ne 0 ]; then
    if [ "$boardid" = "3448" ]; then
	signed_binargs="--bins \"EBT cboot.bin.encrypt;DTB ${dtb_file}.encrypt\""
	signed_rcm_binargs="--bins \"EBT cboot.bin.encrypt;DTB rcm_${dtb_file}.encrypt\""
    fi
    rcm_signcmd="python3 $flashapp --bct \"$sdramcfg_file\" --applet nvtboot_recovery.bin --cfg rcm-flash.xml --chip 0x21 --cmd \"sign\""
    eval "$rcm_signcmd" || exit 1
    cp signed/rcm_${dtb_file}.encrypt .
    cp signed/${RCM_TBCFILENAME}.encrypt .
    cp signed/tos-mon-only.img.encrypt .
    cp signed/rcm_1_encrypt.rcm .
    rcm_bootcmd="python3 $(basename $flashapp) ${inst_args} --bct \"$(basename $sdramcfg_file .cfg).bct\" --odmdata $odmdata \
--bl ${RCM_TBCFILENAME}.encrypt --bldtb rcm_${dtb_file}.encrypt --applet rcm_1_encrypt.rcm --cfg rcm-flash.xml --chip 0x21 \
--lnx $kernfile --tos tos-mon-only.img.encrypt --eks eks.img --kerneldtb rcm_${dtb_file}.encrypt $signed_rcm_binargs $boardcfg --securedev \
--cmd \"rcmboot\""
    if [ $rcm_boot -ne 0 ]; then
	if [ $no_flash -ne 0 ]; then
	    echo "$rcm_bootcmd" > flashcmd.txt
	    chmod +x flashcmd.txt
	    rm -f APPFILE DATAFILE
	    exit 0
	else
	    eval "$rcm_bootcmd" || exit 1
	    exit 0
	fi
    elif [ $to_sign -ne 0 ]; then
	echo "#!/bin/sh" > rcmbootcmd.txt
	echo "$rcm_bootcmd" >> rcmbootcmd.txt
	chmod +x rcmbootcmd.txt
	eval "$flashcmd" || exit 1
	cp signed/flash.xml secureflash.xml
	cp signed/*.encrypt .
	cp signed/rcm_*_encrypt.rcm .
	echo "python3 $(basename $flashapp) ${inst_args}  --bct \"$(basename $sdramcfg_file .cfg).bct\" --odmdata $odmdata \
--bl ${TBCFILENAME}.encrypt --bldtb ${dtb_file}.encrypt --applet rcm_1_encrypt.rcm --cfg secureflash.xml --chip 0x21 $signed_binargs \
--cmd \"secureflash;reboot\"" > flashcmd.txt
	chmod +x flashcmd.txt
	rm -f APPFILE DATAFILE
    fi
    exit 0
fi

if [ $no_flash -ne 0 ]; then
    echo "$flashcmd" | sed -e 's,--skipuid,,g' > flashcmd.txt
    chmod +x flashcmd.txt
    rm -f APPFILE DATAFILE
else
    eval "$flashcmd" || exit 1
    if [ -n "$sdcard" ]; then
	if [ -n "$pre_sdcard_sed" ]; then
	    rm -f signed/flash.xml.in
	    mv signed/flash.xml signed/flash.xml.in
	    sed $pre_sdcard_sed  signed/flash.xml.in > signed/flash.xml
	fi
	$here/make-sdcard $make_sdcard_args signed/flash.xml "$@" || exit 1
    fi
fi
