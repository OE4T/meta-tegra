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
  config-file             Name of the flash.xml file with the SDcard partition definitions
  output                  Either the name of an SDcard device, or the name of an SDcard image file to create

Options:
  -h                      Displays this usage information
  -s size                 Sets size of SDcard image when creating an image file (required)
  -b basename             Base filename for SDcard image (required if no output specified)
  -y                      Skip prompting for confirmation
  --honor-start-locations Use the start locations emitted by nvflashxmlparse
  --no-final-part         Skip special handling of final partition
  --serial-number <sn>    Select USB /dev/sd[a-z] device based on serial number
  --keep-connection       Do not disconnect USB drive after use

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
    local appidx app_b_idx pline i
    if [ -n "$ignore_finalpart" ]; then
	FINALPART=999
	return 0
    fi
    i=0
    for pline in "${PARTS[@]}"; do
	eval "$pline"
	if [ $partfilltoend -eq 1 ]; then
	    FINALPART=$i
	    return 0
	fi
	if [ "$partname" = "APP" ]; then
	    appidx=$i
	elif [ "$partname" = "APP_b" ]; then
	    app_b_idx=$i
	fi
	i=$(expr $i + 1)
    done
    if [ -n "$appidx" ]; then
	if [ -n "$app_b_idx" ]; then
	    ignore_finalpart=yes
	    FINALPART=999
	    return 0
	fi
	FINALPART=$appidx
	return 0
    fi
    echo "ERR: no final partition found" >&2
    return 1
}

make_partitions() {
    local blksize partnumber partname partsize partfile partguid partfilltoend start_location
    local i pline alignarg sgdiskcmd parttype
    if [ "$use_start_locations" = "yes" ]; then
	alignarg="-a 1"
    fi
    sgdiskcmd="sgdisk \"$output\" $alignarg"
    i=0
    for pline in "${PARTS[@]}"; do
	if [ $i -ne $FINALPART ]; then
	    eval "$pline"
	    [ -n "$parttype" ] || parttype="0700"
	    if [ "$use_start_locations" != "yes" ]; then
		start_location=0
	    fi
	    printf "  [%02d] name=%s start=%s size=%s sectors\n" $partnumber $partname $start_location $partsize
	    sgdiskcmd="$sgdiskcmd --new=$partnumber:$start_location:+$partsize --typecode=$partnumber:$parttype -c $partnumber:$partname"
	fi
	i=$(expr $i + 1)
    done
    if [ -z "$ignore_finalpart" ]; then
	eval "${PARTS[$FINALPART]}"
	[ -n "$parttype" ] || parttype="8300"
	if [ "$use_start_locations" != "yes" ]; then
	    start_location=0
	fi
	printf "  [%02d] name=%s (fills to end)\n" $partnumber $partname
	sgdiskcmd="$sgdiskcmd --largest-new=$partnumber --typecode=$partnumber:$parttype -c $partnumber:$partname"
    fi
    local errlog=$(mktemp)
    if ! eval "$sgdiskcmd" >/dev/null 2>"$errlog"; then
	echo "ERR: partitioning failed" >&2
	cat "$errlog" >&2
	rm -f "$errlog"
	return 1
    fi
    rm -f "$errlog"
    return 0
}

create_filesystems() {
    local partnumber
    local pline mke2fscmd fstype
    local errlog=$(mktemp)
    for pline in "${PARTS[@]}"; do
	eval "$pline"
	if [ -n "$fstype" ] && [ "$fstype" != "basic" ]; then
	    printf "Creating $fstype filesystem to /dev/$DEVNAME$PARTSEP$partnumber"
	    mke2fscmd="mkfs.$fstype /dev/$DEVNAME$PARTSEP$partnumber"
	    if ! eval "$mke2fscmd" >/dev/null 2>"$errlog"; then
		    echo "ERR: filesystem failed" >&2
		    cat "$errlog" >&2
		    rm -f "$errlog"
		    return 1
	    fi
	fi
    done
    rm -f "$errlog"
    return 0
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

unmount_device() {
    local dev="$1"
    local mnt=$(cat /proc/mounts | grep "^$dev" | cut -d' ' -f2)
    if [ -n "$mnt" ]; then
        if ! umount "${mnt}" > /dev/null 2>&1; then
            echo "ERR: unmount ${mnt} on device $dev failed" >&2
            return 1
        fi
    fi
    return 0
}

write_partitions_to_device() {
    local blksize partnumber partname partsize partfile partguid partfilltoend
    local i dest pline destsize filesize n_written
    n_written=0
    i=0
    for pline in "${PARTS[@]}"; do
	if [ $i -eq $FINALPART ]; then
	    i=$(expr $i + 1)
	    continue
	fi
	eval "$pline"
	if [ -z "$partfile" ]; then
	    i=$(expr $i + 1)
	    continue
	fi
	if [ -e "signed/$partfile" ]; then
	    partfile="signed/$partfile"
	elif [ ! -e "$partfile" ]; then
	    echo "ERR: cannot find file $partfile for partition $partnumber" >&2
	    return 1
	fi
	filesize=$(stat -c "%s" "$partfile")
	dest="/dev/$DEVNAME$PARTSEP$partnumber"
	if [ ! -b "$dest" ]; then
	    echo "ERR: cannot locate block device $dest" >&2
	    return 1
	fi
    if ! unmount_device "$dest"; then
        echo "ERR: device unmount failed" >&2
        return 1
    fi
	destsize=$(blockdev --getsize64 "$dest" 2>/dev/null)
	if [ $n_written -eq 0 -a -z "$destsize" ]; then
	    sleep 1
	    destsize=$(blockdev --getsize64 "$dest" 2>/dev/null)
	fi
	echo "  Writing $partfile (size=$filesize) to $dest (size=$destsize)..."
	if ! copy_to_device "$partfile" "$dest"; then
	    echo "ERR: failed to write $partfile to $dest" >&2
	    return 1
	fi
	n_written=$(expr $n_written + 1)
	i=$(expr $i + 1)
    done
    if [ -n "$ignore_finalpart" ]; then
	return 0
    fi
    eval "${PARTS[$FINALPART]}"
    if [ -n "$partfile" ]; then
	if [ ! -e "$partfile" ]; then
	    echo "ERR: cannot find file $partfile for partition $partnumber" >&2
	    return 1
	fi
	filesize=$(stat -c "%s" "$partfile")
	dest="/dev/$DEVNAME$PARTSEP$partnumber"
    if ! unmount_device "$dest"; then
        echo "ERR: device unmount failed" >&2
        return 1
    fi
	if [ ! -b "$dest" ]; then
	    echo "ERR: cannot locate block device $dest" >&2
	    return 1
	fi
	destsize=$(blockdev --getsize64 "$dest" 2>/dev/null)
	if [ $n_written -eq 0 -a -z "$destsize" ]; then
	    sleep 1
	    destsize=$(blockdev --getsize64 "$dest" 2>/dev/null)
	fi
	echo "  Writing $partfile (size=$filesize) to $dest (size=$destsize)..."
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
	echo "  Writing $partfile..."
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
		    ;;
	    esac
	else
	    exit 0
	fi
    done
}

ARGS=$(getopt -l "serial-number:,keep-connection,no-final-part,honor-start-locations" -o "yhs:b:" -n "$me" -- "$@")
if [ $? -ne 0 ]; then
    usage
    exit 1
fi

eval set -- "$ARGS"
unset ARGS

preconfirmed=
outsize=
basename=
wait_for_usb_device=
keep_connection=
serial_number=
ignore_finalpart=
use_start_locations=
while true; do
    case "$1" in
	--serial-number)
	    wait_for_usb_device=yes
	    serial_number="$2"
	    shift 2
	    ;;
	--keep-connection)
	    keep_connection=yes
	    shift
	    ;;
	--no-final-part)
	    ignore_finalpart=yes
	    shift
	    ;;
	--honor-start-locations)
	    use_start_locations=yes
	    shift
	    ;;
	-h)
	    usage
	    exit 0
	    ;;
	-y)
	    preconfirmed=yes
	    shift
	    ;;
	-s)
	    outsize=$(compute_size "$2")
	    shift 2
	    ;;
	-b)
	    basename="$2"
	    shift 2
	    ;;
	--)
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

if [ "$wait_for_usb_device" = "yes" ]; then
    echo -n "Looking for USB storage device from $serial_number..."
    output=
    while [ -z "$output" ]; do
	for candidate in /dev/sd[a-z]; do
	    [ -b "$candidate" ] || continue
	    cand_sernum=$(udevadm info --query=property $candidate | grep '^ID_SERIAL_SHORT=' | cut -d= -f2)
	    if [ "$cand_sernum" = "$serial_number" ]; then
		echo "[$candidate]"
		output="$candidate"
		break
	    fi
	done
	if [ -z "$output" ]; then
	    sleep 1
	    echo -n "."
	fi
    done
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

mapfile PARTS < <("$here/nvflashxmlparse" -t rootfs "$cfgfile")
if [ ${#PARTS[@]} -eq 0 ]; then
    echo "No partition definitions found in $cfgfile" >&2
    exit 1
fi

echo  "Creating partitions"
[ -b "$output" ] || dd if=/dev/zero of="$output" bs=512 count=0 seek=$outsize status=none
if ! sgdisk "$output" --clear --mbrtogpt >/dev/null 2>&1; then
    if ! sgdisk "$output" --zap-all >/dev/null 2>&1; then
	echo "ERR: could not initialize GPT on $output" >&2
	exit 1
    fi
    if ! sgdisk "$output" --clear --mbrtogpt >/dev/null 2>&1; then
	echo "ERR: could not initialize GPT on $output after --zap-all" >&2
	exit 1
    fi
fi

find_finalpart || exit 1
make_partitions || exit 1
if ! sgdisk "$output" --verify >/dev/null 2>&1; then
    echo "ERR: verification failed for $output" >&2
    exit 1
fi
if [ -b "$output" ]; then
    sleep 1
    if ! $SUDO partprobe "$output" >/dev/null 2>&1; then
	echo "ERR: partprobe failed after partitioning $output" >&2
	exit 1
    fi
    sleep 1
    create_filesystems || exit 1
fi
if type -p bmaptool >/dev/null 2>&1; then
    HAVEBMAPTOOL=yes
fi
echo "Writing partitions"
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
if [ "$wait_for_usb_device" = "yes" -a "$keep_connection" != "yes" ]; then
    echo "Disconnecting $output"
    for tries in $(seq 1 30); do
	if udisksctl power-off -b $output 2>/dev/null; then
	    break
	fi
	sleep 1
    done
    if [ $tries -ge 30 ]; then
        echo "WARN: failed to disconnect $output"
    fi
fi

exit 0
