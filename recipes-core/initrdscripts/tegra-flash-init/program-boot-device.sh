#!/bin/sh

BOOTPART_SIZE=
BOOTDEV_TYPE=unset

program_spi_partition() {
    local partname="$1"
    local part_offset="$2"
    local part_size="$3"
    local part_file="$4"
    local file_size=0

    if [ -n "$part_file" ]; then
        file_size=$(stat -c "%s" "$part_file")
        if [ -z "$file_size" ]; then
            echo "ERR: could not retrieve file size of $part_file" >&2
            return 1
        fi
    fi
    if [ $file_size -ne 0 ]; then
        echo "Writing $part_file (size=$file_size) to $partname (offset=$part_offset)"
        if ! mtd_debug write @MTD_DEV@ $part_offset $file_size "$part_file"; then
            return 1
        fi
    fi
    # Multiple copies of the BCT get installed at erase-block boundaries
    # within the defined BCT partition
    if [ "$partname" = "BCT" ]; then
        local slotsize=16384
        local rounded_slot_size=$(expr \( \( $slotsize \+ 511 \) / 512 \) \* 512)
        local curr_offset=$(expr $part_offset \+ $rounded_slot_size)
        local copycount=$(expr $part_size / $rounded_slot_size)
        local i=1
        while [ $i -lt $copycount ]; do
            echo "Writing $part_file to BCT+$i (offset=$curr_offset)"
            if ! mtd_debug write @MTD_DEV@ $curr_offset $file_size "$part_file"; then
                return 1
            fi
            i=$(expr $i \+ 1)
            curr_offset=$(expr $curr_offset \+ $rounded_slot_size)
        done
    fi
    return 0
}

program_mmcboot_partition() {
    local partname="$1"
    local part_offset="$2"
    local part_size="$3"
    local part_file="$4"
    local file_size=0
    local bootpart="/dev/mmcblk0boot0"

    if [ -z "$BOOTPART_SIZE" ]; then
        echo "ERR: boot partition size not set" >&2
        return 1
    fi
    if [ $part_offset -ge $BOOTPART_SIZE ]; then
        part_offset=$(expr $part_offset - $BOOTPART_SIZE)
        bootpart="/dev/mmcblk0boot1"
    fi
    if [ -n "$part_file" ]; then
        file_size=$(stat -c "%s" "$part_file")
        if [ -z "$file_size" ]; then
            echo "ERR: could not retrieve file size of $part_file" >&2
            return 1
        fi
    fi
    if [ $file_size -ne 0 ]; then
        echo "Writing $part_file (size=$file_size) to $partname on $bootpart (offset=$part_offset)"
        if ! dd if="$part_file" of="$bootpart" bs=1 seek=$part_offset count=$file_size > /dev/null; then
            return 1
        fi
        # Multiple copies of the BCT get installed at 16KiB boundaries
        # within the defined BCT partition
        if [ "$partname" = "BCT" ]; then
            local slotsize=16384
            local curr_offset=$(expr $part_offset \+ $slotsize)
            local copycount=$(expr $part_size / $slotsize)
            local i=1
            while [ $i -lt $copycount ]; do
                echo "Writing $part_file (size=$file_size) to BCT+$i (offset=$curr_offset)"
                if ! dd if="$part_file" of="$bootpart" bs=1 seek=$curr_offset count=$file_size > /dev/null; then
                    return 1
                fi
                i=$(expr $i \+ 1)
                curr_offset=$(expr $curr_offset \+ $slotsize)
            done
        fi
    fi
    return 0
}

program_boot_partitions() {
    local blpkgdir="$1"
    local partname part_offset part_size part_file
    local rc=0

    if [ -z "$blpkgdir" -o ! -d "$blpkgdir" ]; then
        echo "ERR: missing or non-directory: $blpkgdir" >&2
        return 1
    fi
    if [ -f "$blpkgdir/boot_device_type" ]; then
        BOOTDEV_TYPE=$(cat "$blpkgdir/boot_device_type")
    else
        echo "ERR: missing boot_device_type file in $blpkgdir" >&2
        return 1
    fi
    if [ ! -f "$blpkgdir/partitions.conf" ]; then
        echo "ERR: partition config missing in $blpkgdir" >&2
        return 1
    fi

    if [ "$BOOTDEV_TYPE" = "mmcboot" ]; then
        if [ ! -b /dev/mmcblk0boot0 -o ! -b /dev/mmcblk0boot1 ]; then
            echo "ERR: eMMC boot device, but mmcblk0bootX devices do not exist" >&2
            return 1
        fi
        BOOTPART_SIZE=$(expr $(cat /sys/block/mmcblk0boot0/size) \* 512)
        echo "0" > /sys/block/mmcblk0boot0/force_ro
        echo "0" > /sys/block/mmcblk0boot1/force_ro
        blkdiscard -f /dev/mmcblk0boot0
        blkdiscard -f /dev/mmcblk0boot1
    elif [ "$BOOTDEV_TYPE" = "spi" ]; then
        if [ ! -e @MTD_DEV@ ]; then
            echo "ERR: SPI boot device, but @MTD_DEV@ device does not exist" >&2
            return 1
        fi
        echo "Erasing @MTD_DEV@"
        flash_erase @MTD_DEV@ 0 0
    else
        echo "ERR: unknown boot device type: $BOOTDEV_TYPE" >&2
        return 1
    fi

    oldwd="$PWD"
    cd "$blpkgdir"

    while IFS=":" read partname part_offset part_size part_file; do
        if [ "$BOOTDEV_TYPE" = "spi" ]; then
             if ! program_spi_partition "$partname" "$part_offset" "$part_size" "$part_file"; then
                rc=1
                break
            fi
        elif [ "$BOOTDEV_TYPE" = "mmcboot" ]; then
            if ! program_mmcboot_partition "$partname" "$part_offset" "$part_size" "$part_file"; then
                rc=1
                break
            fi
        fi
    done < partitions.conf
    if [ "$BOOTDEV_TYPE" = "mmcboot" ]; then
        echo "1" > /sys/block/mmcblk0boot0/force_ro
        echo "1" > /sys/block/mmcblk0boot1/force_ro
    fi
    cd "$oldwd"
    return $rc
}

program_boot_partitions "$@"
