#!/bin/bash

set -e

me=$(basename "$0")
here=$(readlink -f $(dirname "$0"))
declare -a PARTS
FINALPART=
DEVNAME=
PARTSEP=
OUTSYSBLK=
HAVEBMAPTOOL=

SUDO=
[ $(id -u) -eq 0 ] || SUDO="sudo"

usage() {
    cat <<EOF

Usage:
  $me [options] [--] <config-file> <output>

Parameters:
  config-file           Name of the flash.xml file with the SDcard partition definitions
  output                Either the name of an SDcard device, or the name of an SDcard image file to create

Options:
  -h                    Displays this usage information
  -s size               Sets size of SDcard image when creating an image file (required)
  -b basename           Base filename for SDcard image (required if no output specified)
  -y                    Skip prompting for confirmation

Confirmation is required if <output> is a device or if it is the name of
a file that already exists.

Size is specified either as number of 512-byte blocks, or can end with
'K', 'M', or 'G' to specify kilo-, mega-, or gigabytes (1000-based rather
than 1024-based). 1% will be subtracted to allow for some overhead on
the card the image will be written to.
EOF
}

compute_size() {
    local s="$1"
    local sfx="${s: -1}"
    if [ "$sfx" = "G" -o "$sfx" = "K" -o "$sfx" = "M" ]; then
	s="${s:0:-1}"
	case "$sfx" in
	    K)
		s=$(expr $s \* 1000)
		;;
	    M)
		s=$(expr $s \* 1000 \* 1000)
		;;
	    G)
		s=$(expr $s \* 1000 \* 1000 \* 1000)
		;;
	esac
	expr \( $s \* 99 / 100 \+ 511 \) / 512
	return 0
    fi
    echo "$s"
    return 0
}

find_finalpart() {
    local blksize partnumber partname partsize partfile partguid partfilltoend
    local appidx pline i
    i=0
    for pline in "${PARTS[@]}"; do
	eval "$pline"
	if [ $partfilltoend -eq 1 ]; then
	    FINALPART=$i
	    return 0
	fi
	if [ "$partname" = "APP" ]; then
	    appidx=$i
	fi
	i=$(expr $i + 1)
    done
    if [ -n "$appidx" ]; then
	FINALPART=$appidx
	return 0
    fi
    echo "ERR: no final partition found" >&2
    return 1
}

make_partitions() {
    local blksize partnumber partname partsize partfile partguid partfilltoend
    local i pline
    i=0
    for pline in "${PARTS[@]}"; do
	if [ $i -ne $FINALPART ]; then
	    eval "$pline"
	    echo -n "$partname..."
	    sgdisk "$output" --new=$partnumber:0:+$partsize --typecode=$partnumber:8300 -c $partnumber:$partname >/dev/null 2>&1
	fi
	i=$(expr $i + 1)
    done
    eval "${PARTS[$FINALPART]}"
    echo -n "$partname..."
    sgdisk "$output" --largest-new=$partnumber --typecode=$partnumber:8300 -c $partnumber:$partname >/dev/null 2>&1
}

copy_to_device() {
    local src="$1"
    local dst="$2"
    if [ -z "$HAVEBMAPTOOL" ]; then
	dd if="$src" of="$dst" conv=fsync status=none >/dev/null 2>&1 || return 1
	return 0
    fi
    local bmap=$(mktemp)
    local rc=0
    bmaptool create -o "$bmap" "$src" >/dev/null 2>&1 || rc=1
    if [ $rc -eq 0 ]; then
	$SUDO bmaptool copy --bmap "$bmap" "$src" "$dst" >/dev/null 2>&1 || rc=1
    fi
    rm "$bmap"
    return $rc
}

write_partitions_to_device() {
    local blksize partnumber partname partsize partfile partguid partfilltoend
    local i dest pline
    i=0
    for pline in "${PARTS[@]}"; do
	if [ $i -eq $FINALPART ]; then
	    i=$(expr $i + 1)
	    continue
	fi
	eval "$pline"
	[ -n "$partfile" ] || continue
	if [ -e "signed/$partfile" ]; then
	    partfile="signed/$partfile"
	elif [ ! -e "$partfile" ]; then
	    echo "ERR: cannot find file $partfile for partition $partnumber" >&2
	    return 1
	fi
	dest="/dev/$DEVNAME$PARTSEP$partnumber"
	if [ ! -b "$dest" ]; then
	    echo "ERR: cannot locate block device $dest" >&2
	    return 1
	fi
	echo -n "$partname..."
	if ! copy_to_device "$partfile" "$dest"; then
	    echo "ERR: failed to write $partfile to $dest" >&2
	    return 1
	fi
	i=$(expr $i + 1)
    done
    eval "${PARTS[$FINALPART]}"
    if [ -n "$partfile" ]; then
	if [ ! -e "$partfile" ]; then
	    echo "ERR: cannot find file $partfile for partition $partnumber" >&2
	    return 1
	fi
	dest="/dev/$DEVNAME$PARTSEP$partnumber"
	if [ ! -b "$dest" ]; then
	    echo "ERR: cannot locate block device $dest" >&2
	    return 1
	fi
	echo -n "$partname..."
	if ! copy_to_device "$partfile" "$dest"; then
	    echo "ERR: failed to write $partfile to $dest" >&2
	    return 1
	fi
    fi
}

write_partitions_to_image() {
    local -a partstart
    local blksize partnumber partname partsize partfile partguid partfilltoend
    local i s e stuff partstart partend pline

    while read partnumber s e stuff; do
	  partstart[$partnumber]=$s
    done < <(sgdisk "$output" --print | egrep '^ +[0-9]')

    i=0
    for pline in "${PARTS[@]}"; do
	eval "$pline"
	[ -n "$partfile" ] || continue
	if [ -e "signed/$partfile" ]; then
	    partfile="signed/$partfile"
	elif [ ! -e "$partfile" ]; then
	    echo "ERR: cannot find file $partfile for partition $partnumber" >&2
	    return 1
	fi
	echo -n "$partname..."
	if ! dd if="$partfile" of="$output" conv=notrunc seek=${partstart[$partnumber]} status=none >/dev/null 2>&1; then
	    echo "ERR: failed to write $partfile to $output (offset ${partstart[$partnumber]}" >&2
	    return 1
	fi
	i=$(expr $i + 1)
    done
}

confirm() {
    while true; do
	if read -p "About to make an SDcard image on $1. OK? "; then
	    case "${REPLY^^}" in
		Y|YES)
		    return 0
		    ;;
		N|NO)
		    exit 0
		    ;;
		*)
		    echo "Please answer 'yes' or' no'."
		    break;
		    ;;
	    esac
	else
	    exit 0
	fi
    done
}

ARGS=$(getopt -o "yhs:b:" -n "$me" -- "$@")
if [ $? -ne 0 ]; then
    usage
    exit 1
fi

eval set -- "$ARGS"
unset ARGS

preconfirmed=
outsize=
basename=
while true; do
    case "$1" in
	'-h')
	    usage
	    exit 0
	    ;;
	'-y')
	    preconfirmed=yes
	    shift
	    ;;
	'-s')
	    outsize=$(compute_size "$2")
	    shift 2
	    ;;
	'-b')
	    basename="$2"
	    shift 2
	    ;;
	'--')
	    shift
	    break
	    ;;
	*)
	    echo "Error processing arguments" >&2
	    exit 1
	    ;;
    esac
done

if [ ! -e "$here/nvflashxmlparse" ]; then
    echo "ERR: this script requires nvflashxmlparse to exist in the same directory" >&2
    exit 1
fi

cfgfile="$1"
output="$2"

if [ -z "$cfgfile" ]; then
    echo "ERR: missing flash config file parameter" >&2
    exit 1
fi

if [ -z "$output" ]; then
    if [ -z "$basename" ]; then
	echo "ERR: missing <output> parameter and no base name specified for SDcard image" >&2
	usage
	exit 1
    fi
    output="${basename}.sdcard"
fi

if [ -c "$output" ]; then
   echo "ERR: $output is a character device" >&2
   exit 1
fi
if [ -b "$output" ]; then
    realoutput=$(readlink -f "$output")
    DEVNAME=$(basename "$realoutput")
    if [ $(dirname "$realoutput") != "/dev" -o ! -e "/sys/block/$DEVNAME" ]; then
	echo "ERR: $output does not appear to be an appropriate device" >&2
	exit 1
    fi
    enddigits=$(echo "$DEVNAME" | sed -r -e's,[a-z]+([0-9]*),\1,')
    [ -z "$enddigits" ] || PARTSEP="p"
    OUTSYSBLK="/sys/block/$DEVNAME"
    outsize=$(cat "$OUTSYSBLK/size")
    [ -n "$preconfirmed" ] || confirm "$output"
else
    if [ -e "$output" ]; then
	[ -n "$preconfirmed" ] || confirm "$output"
	rm "$output"
    fi
    if [ -z "$outsize" ]; then
	echo "ERR: no size specified for SDcard image $output" >&2
	exit 1
    fi
fi

mapfile PARTS < <("$here/nvflashxmlparse" -t sdcard "$cfgfile")
if [ ${#PARTS[@]} -eq 0 ]; then
    echo "No partition definitions found in $cfgfile" >&2
    exit 1
fi

echo -n "Init..."
[ -b "$output" ] || dd if=/dev/zero of="$output" bs=512 count=0 seek=$outsize status=none
if ! sgdisk "$output" --clear --mbrtogpt >/dev/null 2>&1; then
    echo "ERR: could not initialize GPT on $output" >&2
    exit 1
fi

find_finalpart || exit 1
make_partitions || exit 1
echo "[OK]"
if ! sgdisk "$output" --verify >/dev/null 2>&1; then
    echo "ERR: verification failed for $output" >&2
    exit 1
fi
if [ -b "$output" ]; then
    if ! $SUDO partprobe "$output" >/dev/null 2>&1; then
	echo "ERR: partprobe failed after partitioning $output" >&2
	exit 1
    fi
    sleep 1
fi
if type -p bmaptool >/dev/null 2>&1; then
    HAVEBMAPTOOL=yes
fi
echo -n "Writing..."
if [ -b "$output" ]; then
    write_partitions_to_device || exit 1
else
    write_partitions_to_image || exit 1
    if ! sgdisk "$output" --verify >/dev/null 2>&1; then
	echo "ERR: verification failed for $output" >&2
	exit 1
    fi
fi
echo "[OK: $output]"
exit 0
