slotsfx=""
mayberoot=""
foundslotsfx="no"
for bootarg in `cat /proc/cmdline`; do
    case "$bootarg" in
        boot.slot_suffix=*) slotsfx="${bootarg##boot.slot_suffix=}"; foundslotsfx="yes" ;;
        root=*) mayberoot="${bootarg##root=}" ;;
        ro) opt="ro" ;;
        rootwait) wait="yes" ;;
        rootfstype=*) fstype="${bootarg##rootfstype=}" ;;
    esac
done

if [ "$foundslotsfx" != "yes" ]; then
    # This variable file contains a type (should be 6) followed by the value (should be 0 or 1)
    bcdata=$(echo $(hexdump -n 8 -e '2/4 " %u"' /sys/firmware/efi/efivars/BootChainOsCurrent-781e084c-a330-417c-b678-38e696380cb9 2>/dev/null))
    if [ "$(echo "$bcdata" | cut -d' ' -f1)" = "6" ]; then
        slotnum=$(echo "$bcdata" | cut -d' ' -f2)
        [ "$slotnum" != "1" ] || slotsfx="_b"
    fi

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
fi

if [ -z "$rootdev" ]; then
    echo "[FAIL]"
    if [ -n "$mayberoot" ]; then
        if [ "`echo $mayberoot | cut -c1-5`" = "UUID=" ]; then
            rootuuid=`echo $mayberoot | cut -c6-`
            rootdev="`blkid -t UUID=$rootuuid -l | awk -F: '{ print $1 }'`"
        elif [ "`echo $mayberoot | cut -c1-9`" = "PARTUUID=" ]; then
            rootpartuuid=`echo $mayberoot | cut -c10-`
            rootdev="`blkid -t PARTUUID=$rootpartuuid -l | awk -F: '{ print $1 }'`"
        elif [ "`echo $mayberoot | cut -c1-10`" = "PARTLABEL=" ]; then
            rootpartlabel=`echo $mayberoot | cut -c11-`
            rootdev="`blkid -t PARTLABEL=$rootpartlabel -l | awk -F: '{ print $1 }'`"
        elif [ "`echo $mayberoot | cut -c1-6`" = "LABEL=" ]; then
            rootlabel=`echo $mayberoot | cut -c7-`
            rootdev="`blkid -t LABEL=$rootlabel -l | awk -F: '{ print $1 }'`"
        else
            rootdev="$mayberoot"
        fi
    else
        rootdev="/dev/@@TNSPEC_BOOTDEV@@"
    fi
fi
