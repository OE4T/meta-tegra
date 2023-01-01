#!/bin/sh
PATH=/sbin:/bin:/usr/sbin:/usr/bin
mount -t proc proc -o nosuid,nodev,noexec /proc
mount -t devtmpfs none -o nosuid /dev
mount -t sysfs sysfs -o nosuid,nodev,noexec /sys
mount -t configfs configfs -o nosuid,nodev,noexec /sys/kernel/config

[ ! /usr/sbin/wd_keepalive ] || /usr/sbin/wd_keepalive &

sernum=$(cat /sys/devices/platform/efuse-burn/ecid 2>/dev/null)
[ -n "$sernum" ] || sernum=$(cat /sys/module/tegra_fuse/parameters/tegra_chip_uid 2>/dev/null)
if [ -n "$sernum" ]; then
    # Restricted to 8 characters for the ID_MODEL tag
    sernum=$(printf "%x" "$sernum" | tail -c8)
fi
[ -n "$sernum" ] || sernum="UNKNOWN"
UDC=$(ls -1 /sys/class/udc | head -n 1)

wait_for_storage() {
    local file_or_dev="$1"
    local message="Waiting for $file_or_dev..."
    local tries
    for tries in $(seq 1 15); do
	if [ -e "$file_or_dev" ]; then
	    if [ "$message" = "." ]; then
		echo "[OK]"
	    else
		echo "Found $file_or_dev"
	    fi
	    break
	fi
	echo -n "$message"
	message="."
	sleep 1
    done
    if [ $tries -ge 15 ]; then
	echo "[FAIL]"
	return 1
    fi
    return 0
}

setup_usb_export() {
    local storage_export="$1"
    local export_name="$2"
    wait_for_storage "$storage_export" || return 1
    if [ -e /sys/kernel/config/usb_gadget/l4t ]; then
	gadget-vid-pid-remove 1d6b:104
    fi
    sed -e"s,@SERIALNUMBER@,$sernum," -e"s,@STORAGE_EXPORT@,$storage_export," /etc/initrd-flash/initrd-flash.scheme.in > /run/initrd-flash.scheme
    chmod 0644 /run/initrd-flash.scheme
    gadget-import l4t /run/initrd-flash.scheme
    printf "%-8s%-16s" "$export_name" "$sernum" > /sys/kernel/config/usb_gadget/l4t/functions/mass_storage.l4t_storage/lun.0/inquiry_string
    echo "$UDC" > /sys/kernel/config/usb_gadget/l4t/UDC
    if [ -e /sys/class/usb_role/usb2-0-role-switch/role ]; then
	echo "device" > /sys/class/usb_role/usb2-0-role-switch/role
    fi
    echo "Exported $storage_export as $export_name"
    return 0
}

wait_for_connect() {
    local suspended
    local count=0
    echo -n "Waiting for host to connect..."
    while true; do
	suspended=$(expr $(cat /sys/class/udc/$UDC/device/gadget/suspended) \+ 0)
	if [ $suspended -eq 0 ]; then
	    echo "[connected]"
	    break
	fi
	sleep 1
	count=$(expr $count \+ 1)
	if [ $count -ge 5 ]; then
	    echo -n "."
	    count=0
	fi
    done
    return 0
}

wait_for_disconnect() {
    local suspended
    local count=0
    echo -n "Waiting for host to disconnect..."
    while true; do
	suspended=$(expr $(cat /sys/class/udc/$UDC/device/gadget/suspended) \+ 0)
	if [ $suspended -eq 1 ]; then
	    echo "[disconnected]"
	    break
	fi
	sleep 1
	count=$(expr $count \+ 1)
	if [ $count -ge 5 ]; then
	    echo -n "."
	    count=0
	fi
    done
    echo "" > /sys/kernel/config/usb_gadget/l4t/UDC
    return 0
}

get_flash_package() {
    rm -rf /tmp/flashpkg_tree
    mkdir -p /tmp/flashpkg_tree/flashpkg/logs
    # Top of mount point on host will only be writable by root, so
    # create a world-writable subdirectory so a user can send us
    # the commands and content.
    chmod 777 /tmp/flashpkg_tree/flashpkg
    echo "PENDING: expecting command sequence from host" > /tmp/flashpkg_tree/flashpkg/status
    dd if=/dev/zero of=/tmp/flashpkg.ext4 bs=1M count=128 > /dev/null || return 1
    mke2fs -t ext4 -d /tmp/flashpkg_tree /tmp/flashpkg.ext4 > /dev/null || return 1
    setup_usb_export /tmp/flashpkg.ext4 flashpkg || return 1
    wait_for_connect || return 1
    wait_for_disconnect || return 1
    return 0
}

process_bootloader_package() {
    rm -f /run/bootloader-status
    if program-boot-device /tmp/flashpkg/flashpkg/bootloader; then
	echo "SUCCESS" > /run/bootloader-status
    else
	echo "FAILED" > /run/bootloader-status
    fi
}


skip_status_report=
reboot_type=
final_status="FAILED"
wait_for_bootloader=

if ! get_flash_package; then
    echo "Error retrieving flashing package" >&2
    skip_status_report=yes
else
    mkdir -p /tmp/flashpkg
    mount -t ext4 /tmp/flashpkg.ext4 /tmp/flashpkg
fi

if [ ! -e /tmp/flashpkg/flashpkg/conf/command_sequence ]; then
    echo "No command sequence in flash package, nothing to do"
else
    exit_early=
    while read cmd args; do
	[ -z "$exit_early" ] || break
	echo "Processing: $cmd $args"
	case "$cmd" in
	    bootloader)
		process_bootloader_package 2>&1 > /tmp/flashpkg/flashpkg/logs/bootloader.log &
		wait_for_bootloader=yes
		;;
	    erase-mmc)
		if [ -b /dev/mmcblk0 ]; then
		    blkdiscard -f /dev/mmcblk0 2>&1 > /tmp/flashpkg/flashpkg/logs/erase-mmc.log
		else
		    echo "/dev/mmcblk0 does not exist, skipping" > /tmp/flashpkg/flashpkg/logs/erase-mmc.log
		fi
		;;
	    export-devices)
		for dev in $args; do
		    if setup_usb_export /dev/$dev $dev 2>&1 > /tmp/flashpkg/flashpkg/logs/export-$dev.log; then
			if wait_for_connect 2>&1 >> /tmp/flashpkg/flashpkg/logs/export-$dev.log; then
			    if wait_for_disconnect 2>&1 >> /tmp/flashpkg/flashpkg/logs/export-$dev.log; then
				sgdisk /dev/$dev --verify 2>&1 >> /tmp/flashpkg/flashpkg/logs/export-$dev.log
				sgdisk /dev/$dev --print 2>&1 >> /tmp/flashpkg/flashpkg/logs/export-$dev.log
				continue
			    fi
			fi
		    fi
		    echo "Export of $dev failed" >&2
		    exit_early=yes
		    break
		done
		;;
	    reboot)
		reboot_type="$args"
		final_status="SUCCESS"
		# reboot command is expected to be the last in the sequence, if present
		break
		;;
	    *)
		echo "Unrecognized command: $cmd $args" > /tmp/flashpkg/flashpkg/logs/commandloop.log
		exit_early="yes"
		;;
	esac
    done < /tmp/flashpkg/flashpkg/conf/command_sequence
fi

if [ -n "$wait_for_bootloader" ]; then
    message="Waiting for boot device programming to complete..."
    while [ ! -e /run/bootloader-status ]; do
	echo -n "$message"
	message="."
	sleep 1
    done
    blstatus=$(cat /run/bootloader-status)
    if [ "$blstatus" = "FAILED" ]; then
       final_status="FAILED"
    fi
fi
echo "$final_status" > /tmp/flashpkg/flashpkg/status
if [ -z "$skip_status_report" ]; then
    umount /tmp/flashpkg && setup_usb_export /tmp/flashpkg.ext4 flashpkg && wait_for_connect && wait_for_disconnect
fi

if [ "$reboot_type" = "forced-recovery" ]; then
    reboot-recovery
else
    reboot -f
fi
