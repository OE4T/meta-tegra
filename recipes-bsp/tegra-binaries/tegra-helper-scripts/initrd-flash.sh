#!/bin/bash

set -o pipefail

me=$(basename "$0")
here=$(readlink -f $(dirname "$0"))

declare -A DEFAULTS

usage() {
    cat <<EOF
Usage:
  $me [options]

Options:
  -h|--help             Displays this usage information
  --skip-bootloader     Skip boot partition programming
  --usb-instance        USB instance of Jetson device
  --erase-nvme          Erase NVME drive during flashing

Options passed through to flash helper:
  -u                    PKC key file for signing
  -v                    SBK key file for signing

EOF
}

# The build must generate these environment settings
if [ ! -e .env.initrd-flash ]; then
    echo "Missing environment settings" >&2
    exit 1
fi

. .env.initrd-flash

# The .presigning-vars file is generated when binaries
# are signed during the build
PRESIGNED=
if [ -e .presigning-vars ]; then
    . .presigning-vars
    PRESIGNED=yes
fi

usb_instance=
instance_args=
keyfile=
sbk_keyfile=
skip_bootloader=0
early_final_status=0
erase_nvme=0

ARGS=$(getopt -n $(basename "$0") -l "usb-instance:,help,skip-bootloader,erase-nvme" -o "u:v:h" -- "$@")
if [ $? -ne 0 ]; then
    usage >&2
    exit 1
fi
eval set -- "$ARGS"
unset ARGS

while true; do
    case "$1" in
	--usb-instance)
	    usb_instance="$2"
	    shift 2
	    ;;
	--skip-bootloader)
	    skip_bootloader=1
	    shift
	    ;;
	--erase-nvme)
	    erase_nvme=1
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
	-h|--help)
	    usage
	    exit 0
	    ;;
	--)
	    shift
	    break
	    ;;
	*)
	    echo "Error processing options" >&2
	    usage
	    exit 1
	    ;;
    esac
done

if [ -n "$PRESIGNED" ]; then
    if [ -n "$keyfile" -o -n "$sbk_keyfile" ]; then
	echo "WARN: binaries already signed; ignoring signing options" >&2
	keyfile=
	sbk_keyfile=
    fi
fi

wait_for_rcm() {
    "$here/find-jetson-usb" --wait "$usb_instance"
}

copy_signed_binaries() {
    local signdir="${1:-signed}"
    local xmlfile="${2:-flash.xml.tmp}"
    local destdir="${3:-.}"
    local blksize partnumber partname partsize partfile partguid parttype partfilltoend
    local line

    while read line; do
	eval "$line"
	[ -n "$partfile" ] || continue
	if [ ! -e "$signdir/$partfile" ]; then
	    if [ ! -e "$destdir/$partfile" ] && ! echo "$partfile" | grep -q "FILE"; then
		echo "ERR: could not copy $partfile from $signdir" >&2
		return 1
	    fi
	else
	    cp "$signdir/$partfile" "$destdir"
	fi
    done < <("$here/nvflashxmlparse" -t boot "$signdir/$xmlfile"; "$here/nvflashxmlparse" -t rootfs "$signdir/$xmlfile")
}

sign_binaries() {
    if [ -n "$PRESIGNED" ]; then
	cp doflash.sh flash_signed.sh
	if ! copy_bootloader_files bootloader_staging; then
	    return 1
	fi
	return 0
    fi
    if [ -z "$BOARDID" -o -z "$FAB" ]; then
	wait_for_rcm
    fi
    rm -rf rcmboot_blob
    if MACHINE=$MACHINE BOARDID=$BOARDID FAB=$FAB BOARDSKU=$BOARDSKU BOARDREV=$BOARDREV CHIPREV=$CHIPREV CHIP_SKU=$CHIP_SKU serial_number=$serial_number \
	      BOOTCONTROL_OVERLAYS=L4TConfiguration-rcmboot.dtbo \
	      "$here/$FLASH_HELPER" --no-flash --rcm-boot -u "$keyfile" -v "$sbk_keyfile" $instance_args \
	      flash.xml.in $DTBFILE $EMMC_BCTS $ODMDATA initrd-flash.img $ROOTFS_IMAGE; then
	    ln -sf "$here/tegrarcm_v2" rcmboot_blob/
	    cat > rcm-boot.sh <<EOF
oldwd="\$PWD"
cd rcmboot_blob
EOF
	    cat rcmboot_blob/rcmbootcmd.txt >> rcm-boot.sh
	    cat >> rcm-boot.sh <<EOF
cd "\$oldwd"
EOF
	    chmod +x rcm-boot.sh
    else
	return 1
    fi
    . ./boardvars.sh
    if MACHINE=$MACHINE BOARDID=$BOARDID FAB=$FAB BOARDSKU=$BOARDSKU BOARDREV=$BOARDREV CHIPREV=$CHIPREV CHIP_SKU=$CHIP_SKU serial_number=$serial_number \
	      "$here/$FLASH_HELPER" --no-flash --sign -u "$keyfile" -v "$sbk_keyfile" $instance_args \
	      flash.xml.in $DTBFILE $EMMC_BCTS $ODMDATA $LNXFILE $ROOTFS_IMAGE; then
	cp flashcmd.txt flash_signed.sh
	sed -i -e's,--cfg secureflash.xml,--cfg internal-secureflash.xml,g' flash_signed.sh
	mv secureflash.xml internal-secureflash.xml
    else
	return 1
    fi
    if ! copy_bootloader_files bootloader_staging; then
	return 1
    fi
    if [ -e external-flash.xml.in ]; then
        if MACHINE=$MACHINE BOARDID=$BOARDID FAB=$FAB BOARDSKU=$BOARDSKU BOARDREV=$BOARDREV CHIPREV=$CHIPREV CHIP_SKU=$CHIP_SKU \
		  "$here/$FLASH_HELPER" --no-flash --sign --external-device -u "$keyfile" -v "$sbk_keyfile" $instance_args \
		  external-flash.xml.in $DTBFILE $EMMC_BCTS $ODMDATA $LNXFILE $ROOTFS_IMAGE; then
	    mv secureflash.xml external-secureflash.xml
	else
	    return 1
	fi
    fi
    return 0
}

prepare_for_rcm_boot() {
    :
}

run_rcm_boot() {
    if [ -z "$BR_CID" ]; then
	if ./rcm-boot.sh | tee rcm-boot.output; then
	    BR_CID=$(grep BR_CID: rcm-boot.output | cut -d: -f2)
	    return 0
	else
	    return 1
	fi
    fi
    ./rcm-boot.sh || return 1
}

mount_partition() {
    local dev="$1"
    local mnt=$(cat /proc/mounts | grep "^$dev" | cut -d' ' -f2)
    local i=$(echo $mnt|awk -F' ' '{print NF}')
    while [ $i -ne 0 ]; do
        local mnt=$(echo ${mnt} | cut -d' ' -f$i)
		if ! umount "${mnt}" > /dev/null 2>&1; then
            echo "ERR: unmount ${mnt} on device $dev failed" >&2
            return 1
        fi
        i=$(expr $i - 1)
    done
    if udisksctl mount -b "$dev" > /dev/null; then
	cat /proc/mounts | grep "^$dev" | cut -d' ' -f2
	return 0
    fi
    echo ""
    return 1
}

unmount_and_release() {
    local mnt="$1"
    local dev="$2"
    local remain=3
    if [ -n "$mnt" ]; then
	udisksctl unmount --force -b "$dev"
    fi
    while [ $remain -gt 0 ]; do
	if udisksctl power-off -b "$dev"; then
	    return 0
	fi
	sleep 1
	remain=$(expr $remain - 1)
    done
    return 1
}

wait_for_usb_storage() {
    local sessid="$1"
    local name="$2"
    local count=0
    local output candidate cand_model cand_vendor

    echo -n "Waiting for USB storage device $name from $sessid..." >&2
    while [ -z "$output" ]; do
	for candidate in /dev/sd[a-z]; do
	    [ -b "$candidate" ] || continue
	    cand_model=$(udevadm info --query=property $candidate | grep '^ID_MODEL=' | cut -d= -f2)
	    if [ "$cand_model" = "$sessid" ]; then
		cand_vendor=$(udevadm info --query=property $candidate | grep '^ID_VENDOR=' | cut -d= -f2)
		if [ "$cand_vendor" = "$name" ]; then
		    echo "[$candidate]" >&2
		    output="$candidate"
		    break
		elif [ "$name" != "flashpkg" -a "$cand_vendor" = "flashpkg" ]; then
		    # This could happen if there was a failure on the device side
		    echo "[got flashpkg when expecting $name]" >&2
		    echo ""
		    early_final_status=1
		    return 1
		fi
	    fi
	done
	if [ -z "$output" ]; then
	    sleep 1
	    count=$(expr $count \+ 1)
	    if [ $count -ge 5 ]; then
		echo -n "." >&2
		count=0
	    fi
	fi
    done
    echo "$output"
    return 0
}

copy_bootloader_files() {
    local dest="$1"
    local partnumber partloc partname start_location partsize partfile partattrs partsha
    local devnum instnum
    local is_spi is_mmcboot
    rm -f "$dest/partitions.conf"
    while IFS=", " read partnumber partloc start_location partsize partfile partattrs partsha; do
	# Need to trim off leading blanks
	devnum=$(echo "$partloc" | cut -d':' -f 1)
	instnum=$(echo "$partloc" | cut -d':' -f 2)
	partname=$(echo "$partloc" | cut -d':' -f 3)
	# SPI is 3:0
	# eMMC boot blocks (boot0/boot1) are 0:3
	# eMMC user is 1:3
	# NVMe (any external device) is 9:0
	if [ $devnum -eq 3 -a $instnum -eq 0 ] || [ $devnum -eq 0 -a $instnum -eq 3 ]; then
	    if [ -n "$partfile" ]; then
		cp "$partfile" "$dest/"
	    fi
	    if [ $devnum -eq 3 -a $instnum -eq 0 ]; then
		is_spi=yes
	    elif [ $devnum -eq 0 -a $instnum -eq 3 ]; then
		is_mmcboot=yes
	    fi
	    echo "$partname:$start_location:$partsize:$partfile" >> "$dest/partitions.conf"
	fi
    done < flash.idx
    if [ -n "$is_spi" ]; then
	if [ -n "$is_mmcboot" ]; then
	    echo "ERR: found bootloader entries for both SPI flash and eMMC boot partitions" >&2
	    return 1
	fi
	echo "spi" > "$dest/boot_device_type"
    elif [ -n "$is_mmcboot" ]; then
	echo "mmcboot" > "$dest/boot_device_type"
    else
	echo "ERR: no SPI or eMMC boot partition entries found" >&2
	return 1
    fi
    return 0
}

generate_flash_package() {
    local dev=$(wait_for_usb_storage "$session_id" "flashpkg")
    local exports

    if [ -z "$dev" ]; then
	echo "ERR: could not locate USB storage device for sending flashing commands" >&2
	return 1
    fi
    local devsize=$(cat /sys/block/$(basename $dev)/size 2>/dev/null)
    echo "Device size in blocks: $devsize" >&2
    local mnt=$(mount_partition "$dev")
    if [ -z "$mnt" ]; then
	echo "ERR: could not mount USB storage for writing flashing commands" >&2
	return 1
    fi

    mkdir "$mnt/flashpkg/conf"
    rm -f "$mnt/flashpkg/conf/command_sequence"
    touch "$mnt/flashpkg/conf/command_sequence"
    if [ $skip_bootloader -eq 0 ]; then
	echo "bootloader" >> "$mnt/flashpkg/conf/command_sequence"
	mkdir "$mnt/flashpkg/bootloader"
	cp bootloader_staging/* "$mnt/flashpkg/bootloader"
    fi

    echo "extra-pre-wipe" >> "$mnt/flashpkg/conf/command_sequence"

    if [ $erase_nvme -eq 1 ]; then
	echo "erase-nvme" >> "$mnt/flashpkg/conf/command_sequence"
    fi
    [ $EXTERNAL_ROOTFS_DRIVE -eq 0 -o $NO_INTERNAL_STORAGE -eq 1 ] || echo "erase-mmc" >> "$mnt/flashpkg/conf/command_sequence"
    echo "export-devices $ROOTFS_DEVICE" >> "$mnt/flashpkg/conf/command_sequence"

    echo "extra" >> "$mnt/flashpkg/conf/command_sequence"
    echo "reboot" >> "$mnt/flashpkg/conf/command_sequence"

    unmount_and_release "$mnt" "$dev" || return 1
}

write_to_device() {
    local devname="$1"
    local flashlayout="$2"
    local dev=$(wait_for_usb_storage "$session_id" "$devname")
    local opts="$3"
    local rewritefiles="internal-secureflash.xml"
    local datased simgname rc=1
    local extraarg

    if [ -z "$dev" ]; then
	echo "ERR: could not find $devname" >&2
	return 1
    fi
    if [ -e external-secureflash.xml ]; then
	rewritefiles="external-secureflash.xml,$rewritefiles"
    fi
    "$here/nvflashxmlparse" --rewrite-contents-from=$rewritefiles -o initrd-flash.xml "$flashlayout"
    if [ -n "$DATAFILE" ]; then
	datased="-es,DATAFILE,$DATAFILE,"
    else
	datased="-e/DATAFILE/d"
    fi
    # XXX
    # For the pre-signed case, the flash layout will contain the
    # name of the sparseimage file, and we need to convert it back to
    # the raw image name.
    # XXX
    simgname="${ROOTFS_IMAGE%.*}.img"
    sed -i -e"s,$simgname,$ROOTFS_IMAGE," -e"s,APPFILE_b,$ROOTFS_IMAGE," -e"s,APPFILE,$ROOTFS_IMAGE," -e"s,DTB_FILE,kernel_$DTBFILE," $datased initrd-flash.xml
    if "$here/make-sdcard" -y $opts $extraarg initrd-flash.xml "$dev"; then
	rc=0
    fi
    if ! unmount_and_release "" "$dev"; then
	rc=1
    fi
    return $rc
}

get_final_status() {
    local dtstamp="$1"
    local dev=$(wait_for_usb_storage "$session_id" "flashpkg")
    local mnt final_status logdir logfile
    if [ -z "$dev" ]; then
	echo "ERR: could not get final status from device" >&2
	return 1
    fi
    mnt=$(mount_partition "$dev")
    if [ -z "$mnt" ]; then
	echo "ERR: could not mount USB device to get final status from device" >&2
	return 1
    fi
    final_status=$(cat $mnt/flashpkg/status)
    if [ -d "$mnt/flashpkg/logs" ]; then
	logdir="device-logs-$dtstamp"
	if [ -d "$logdir" ]; then
	    echo "Logs directory $logdir already exists, replacing" >&2
	    rm -rf "$logdir"
	fi
	mkdir "$logdir"
	for logfile in "$mnt"/flashpkg/logs/*; do
	    [ -f "$logfile" ] || continue
	    cp "$logfile" "$logdir/"
	done
    fi
    unmount_and_release "$mnt" "$dev" || return 1
    echo "Final status: $final_status"
    return 0
}

dtstamp=$(date +"%Y-%m-%d-%H.%M.%S")
logfile="log.initrd-flash.$dtstamp"
stepnumber=1

step_banner() {
    local msg="$1"
    echo "== Step $stepnumber: $msg at $(date -Is) ==" | tee -a "$logfile"
    stepnumber=$(expr $stepnumber \+ 1)
}

echo "Starting at $(date -Is)" | tee "$logfile"
echo "Machine:       ${MACHINE}" | tee "$logfile"
echo "Rootfs device: ${BOOTDEV}" | tee "$logfile"
if ! wait_for_rcm 2>&1 | tee -a "$logfile"; then
    echo "ERR: Device not found at $(date -Is)" | tee -a "$logfile"
    exit 1
fi
if [ -z "$usb_instance" -a -e ".found-jetson" ]; then
    . .found-jetson
fi
if [ -n "$usb_instance" ]; then
    instance_args="--usb-instance $usb_instance"
fi
step_banner "Signing binaries"
rm -rf bootloader_staging
mkdir bootloader_staging
if ! sign_binaries 2>&1 >>"$logfile"; then
    echo "ERR: signing failed at $(date -Is)"  | tee -a "$logfile"
    exit 1
fi
if [ -z "$PRESIGNED" ]; then
    [ ! -f ./boardvars.sh ] || . ./boardvars.sh
fi
step_banner "Boot Jetson via RCM"
if ! prepare_for_rcm_boot 2>&1 >>"$logfile"; then
    echo "ERR: Preparing RCM boot command failed at $(date -Is)" | tee -a "$logfile"
    exit 1
fi
if ! wait_for_rcm 2>&1 | tee -a "$logfile"; then
    echo "ERR: Device not found at $(date -Is)" | tee -a "$logfile"
    exit 1
fi
if ! run_rcm_boot 2>&1 >>"$logfile"; then
    echo "ERR: RCM boot failed at $(date -Is)" | tee -a "$logfile"
    exit 1
fi
[ ! -f ./boardvars.sh ] || . ./boardvars.sh

if [ -z "$serial_number" ]; then
    echo "ERR: did not get device serial number at $(date -Is)" | tee -a "$logfile"
    exit 1
fi

session_id=$(printf "%x" "$serial_number" | tail -c8)

# Boot device flashing
step_banner "Sending flash sequence commands"
if ! generate_flash_package 2>&1 | tee -a "$logfile"; then
    echo "ERR: could not create command package at $(date -Is)" | tee -a "$logfile"
    exit 1
fi
if [ $EXTERNAL_ROOTFS_DRIVE -eq 1 ]; then
    keep_going=1
    step_banner "Writing partitions on external storage device"
    if ! write_to_device $ROOTFS_DEVICE external-flash.xml.in 2>&1 | tee -a "$logfile"; then
	echo "ERR: write failure to external storage at $(date -Is)" | tee -a "$logfile"
	if [ $early_final_status -eq 0 ]; then
	    exit 1
	fi
    fi
else
    step_banner "Writing partitions on internal storage device"
    if ! write_to_device $ROOTFS_DEVICE flash.xml.in 2>&1 | tee -a "$logfile"; then
	echo "ERR: write failure to internal storage at $(date -Is)" | tee -a "$logfile"
	if [ $early_final_status -eq 0 ]; then
	    exit 1
	fi
    fi
fi
step_banner "Waiting for final status from device"
if ! get_final_status "$dtstamp" 2>&1 | tee -a "$logfile"; then
    echo "ERR: failed to retrieve device status at $(date -Is)" | tee -a "$logfile"
    echo "Host-side log:              $logfile"
    echo "Device-side logs stored in: device-logs-$dtstamp"
    exit 1
fi
echo "Successfully finished at $(date -Is)" | tee -a "$logfile"
echo "Host-side log:              $logfile"
echo "Device-side logs stored in: device-logs-$dtstamp"
exit 0
