#!/bin/bash
# -*- mode: shell-script; indent-tabs-mode: nil; sh-basic-offset: 4; -*-
ARGS=$(getopt -n $(basename "$0") -l "wait" -o "" -- "$@")
if [ $? -ne 0 ]; then
    echo "Error parsing options" >&2
    exit 1
fi
eval set -- "$ARGS"
unset ARGS

opt_wait=

while true; do
    case "$1" in
        --wait)
            opt_wait=yes
            shift
            ;;
        --)
            shift
            break
            ;;
        *)
            echo "Error processing options" >&2
            exit 1
            ;;
    esac
done

check_jetson() {
    local usbpath="$1"
    local idVendor idProduct
    idVendor=$(cat "$usbpath/idVendor" 2>/dev/null)
    if [ "$idVendor" = "0955" ]; then
        idProduct=$(cat "$usbpath/idProduct" 2>/dev/null)
        case "$idProduct" in
            7019|7819|7919|7e19|7023|7026|7223|7226|7323|7423|7523|7623)
                if [ -f "$usbpath/busnum" -a -f "$usbpath/devpath" ]; then
                    return 0
                fi
                ;;
            *)
                ;;
        esac
    fi
    return 1
}

find_jetson() {
    local usbpath
    while read usbpath; do
        if check_jetson "$usbpath"; then
            printf "%s-%s" $(cat "$usbpath/busnum") $(cat "$usbpath/devpath")
            return 0
        fi
    done < <(ls -d /sys/bus/usb/devices/*-* 2>/dev/null | grep -v ':')
    return 1
}

find_buspath() {
    usbpath=$(ls -d /sys/bus/usb/devices/$1 2>/dev/null)
    if [ -z "$usbpath" ]; then
        return 1
    fi
    check_jetson "$usbpath"
}

buspath="$1"
if [ -z "$buspath" ]; then
    buspath=$(find_jetson)
    message="Waiting for Jetson to appear on USB..."
    while [ -n "$opt_wait" -a -z "$buspath" ]; do
        echo -n "$message"
        message="."
        sleep 1
        buspath=$(find_jetson)
    done
    if [ -n "$buspath" ]; then
        if [ -n "$opt_wait" -a "$message" = "." ]; then
            echo "[found: $buspath]"
        else
            echo "Found Jetson device in recovery mode at USB $buspath"
        fi
        echo "usb_instance=$buspath" > .found-jetson
        exit 0
    fi
    echo "ERR: No Jetson device in recovery mode found on USB" >&2
    exit 1
else
    find_buspath "$buspath"
    rc=$?
    message="Waiting for Jetson to appear at $buspath..."
    while [ -n "$opt_wait" -a $rc -ne 0 ]; do
        echo -n "$message"
        message="."
        sleep 1
        find_buspath "$buspath"
        rc=$?
    done
    if [ $rc -eq 0 ]; then
        if [ -n "$opt_wait" -a "$message" = "." ]; then
            echo "[found]"
        else
            echo "Found Jetson device in recovery mode at USB $buspath"
        fi
        exit 0
    fi
    echo "ERR: No Jetson device in recovery mode found at USB $buspath" >&2
    exit 1
fi
