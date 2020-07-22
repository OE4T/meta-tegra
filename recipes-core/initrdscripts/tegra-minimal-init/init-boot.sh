#!/bin/sh
PATH=/sbin:/bin:/usr/sbin:/usr/bin
mount -t proc proc /proc
mount -t devtmpfs none /dev
mount -t sysfs sysfs /sys

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
    count=0
    while [ $count -lt 25 ]; do
	test -b "${rootdev}" && break
	sleep 0.1
	count=`expr $count + 1`
    done
fi
echo "Mounting ${rootdev}..."
mount -t "${fstype}" -o "${opt}" "${rootdev}" /mnt || exec sh

[ ! -f /etc/platform-pre-switchroot ] || . /etc/platform-pre-switchroot

echo "Switching to rootfs on ${rootdev}..."
mount --move /sys  /mnt/sys
mount --move /proc /mnt/proc
mount --move /dev  /mnt/dev
exec switch_root /mnt /sbin/init
