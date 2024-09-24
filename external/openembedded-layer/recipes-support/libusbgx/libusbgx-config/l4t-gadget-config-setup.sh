#!/bin/sh
[ -d /sys/module/libcomposite ] || modprobe libcomposite
[ -d /sys/module/bridge ] || modprobe bridge
[ -d /run/usbgx ] || mkdir /run/usbgx
[ ! -e /run/usbgx/l4t.schema ] || exit 0
if [ ! -e /usr/share/usbgx/l4t.schema.in ]; then
    echo "ERR: missing gadget schema template" >&2
    exit 1
fi
sernum=$(cat /proc/device-tree/serial-number 2>/dev/null | tr -d '\000')
[ -n "$sernum" ] || sernum="UNKNOWN"
sed -e"s,@SERIALNUMBER@,$sernum," /usr/share/usbgx/l4t.schema.in > /run/usbgx/l4t.schema
chmod 0644 /run/usbgx/l4t.schema
