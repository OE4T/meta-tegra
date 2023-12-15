#!/bin/sh
PATH=/sbin:/bin:/usr/sbin:/usr/bin
mount -t proc proc -o nosuid,nodev,noexec /proc
mount -t devtmpfs none -o nosuid /dev
mount -t sysfs sysfs -o nosuid,nodev,noexec /sys
mount -t efivarfs efivarfs -o nosuid,nodev,noexec /sys/firmware/efi/efivars

find /sys -name modalias | while read m; do
    modalias=$(cat "$m")
    modprobe -v "$modalias" 2> /dev/null
done

modprobe -v nvme

rootdev=""
opt="rw"
wait=""
fstype="auto"

[ ! -f /etc/platform-preboot ] || . /etc/platform-preboot

if [ -z "$rootdev" ]; then
    for bootarg in `cat /proc/cmdline`; do
        case "$bootarg" in
            root=*) rootdev="${bootarg##root=}" ;;
            ro) opt="ro" ;;
            rootwait) wait="yes" ;;
            rootfstype=*) fstype="${bootarg##rootfstype=}" ;;
        esac
    done
fi

if [ -n "$wait" -a ! -b "${rootdev}" ]; then
    echo "Waiting for ${rootdev}..."
    while true; do
        test -b "${rootdev}" && break
        sleep 0.1
    done
fi

echo "Mounting ${rootdev}..."
[ -d /mnt ] || mkdir -p /mnt
count=0
while [ $count -lt 5 ]; do
    if mount -t "${fstype}" -o "${opt}" "${rootdev}" /mnt; then
        break
    fi
    sleep 1.0
    count=`expr $count + 1`
done
[ $count -lt 5 ] || exec sh

[ ! -f /etc/platform-pre-switchroot ] || . /etc/platform-pre-switchroot

echo "Switching to rootfs on ${rootdev}..."
mount --move /sys  /mnt/sys
mount --move /proc /mnt/proc
mount --move /dev  /mnt/dev
exec switch_root /mnt /sbin/init
