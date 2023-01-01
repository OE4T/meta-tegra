blkid --probe 2>/dev/null
bootpartdev=$(blkid -l -t PARTLABEL=BOOT | cut -d: -f1)
if [ -n "$bootpartdev" ]; then
    echo "Mounting $bootpartdev at /boot..."
    mount "$bootpartdev" /mnt/boot
fi
