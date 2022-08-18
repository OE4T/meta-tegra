slotsfx=""
mayberoot=""
for bootarg in `cat /proc/cmdline`; do
    case "$bootarg" in
	boot.slot_suffix=*) slotsfx="${bootarg##boot.slot_suffix=}" ;;
	root=*) mayberoot="${bootarg##root=}" ;;
	ro) opt="ro" ;;
	rootwait) wait="yes" ;;
    esac
done
rootdev=`blkid -l -t PARTLABEL=APP$slotsfx | cut -d: -f1`
if [ -z "$rootdev" ]; then
    if [ -n "$mayberoot" ]; then
	rootdev="$mayberoot"
    else
	rootdev="/dev/mmcblk0p1"
    fi
fi
