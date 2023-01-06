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
message="Waiting for APP$slotsfx partition..."
for count in $(seq 1 10); do
    blkid --probe 2>/dev/null
    rootdev=`blkid -l -t PARTLABEL=APP$slotsfx | cut -d: -f1`
    if [ -n "$rootdev" ]; then
	echo "[OK: $rootdev]"
	break
    fi
    echo -n "$message"
    message="."
    sleep 0.5
done
if [ -z "$rootdev" ]; then
    echo "[FAIL]"
    if [ -n "$mayberoot" ]; then
	rootdev="$mayberoot"
    else
	rootdev="/dev/@TNSPEC_BOOTDEV@"
    fi
fi
