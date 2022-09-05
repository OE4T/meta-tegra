slotsfx=""
mayberoot=""
foundslotsfx="no"
for bootarg in `cat /proc/cmdline`; do
    case "$bootarg" in
	boot.slot_suffix=*) slotsfx="${bootarg##boot.slot_suffix=}"; foundslotsfx="yes" ;;
	root=*) mayberoot="${bootarg##root=}" ;;
	ro) opt="ro" ;;
	rootwait) wait="yes" ;;
    esac
done
if [ "$foundslotsfx" != "yes" ]; then
    # This variable file contains a type (should be 6) followed by the value (should be 0 or 1)
    bcdata=$(echo $(hexdump -n 8 -e '2/4 " %u"' /sys/firmware/efi/efivars/BootChainOsCurrent-781e084c-a330-417c-b678-38e696380cb9 2>/dev/null))
    if [ "$(echo "$bcdata" | cut -d' ' -f1)" = "6" ]; then
	slotnum=$(echo "$bcdata" | cut -d' ' -f2)
	[ "$slotnum" != "1" ] || slotsfx="_b"
    fi
fi
blkid --probe 2>/dev/null
rootdev=`blkid -l -t PARTLABEL=APP$slotsfx | cut -d: -f1`
if [ -z "$rootdev" ]; then
    sleep 0.5
    blkid --probe 2>/dev/null
    rootdev=`blkid -l -t PARTLABEL=APP$slotsfx | cut -d: -f1`
fi
if [ -z "$rootdev" ]; then
    if [ -n "$mayberoot" ]; then
	rootdev="$mayberoot"
    else
	rootdev="/dev/mmcblk0p1"
    fi
fi
