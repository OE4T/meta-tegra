#!/bin/bash
# -*- mode: shell-script; indent-tabs-mode: nil; sh-basic-offset: 4; -*-

set -o pipefail

me=$(basename "$0")
here=$(readlink -f $(dirname "$0"))

declare -A DEFAULTS

usage() {
    cat <<EOF
Usage:
  $me [options]

Options:
  -D|--debug            Enable debug logging when running unified flash script
  -h|--help             Displays this usage information
  --external-only       Write only the external storage device
  -k|--partition NAME   Write only the specified partition
  --qspi-only           Write only the QSPI flash (boot firmware)
  --usb-instance        USB instance of Jetson device

Options passed through to flash helper:
  -u                    PKC key file for signing
  -v                    SBK key file for signing

Note that --external-only, --qspi-only, and --partition are mutually
exclusive.

EOF
}

# The build must generate these environment settings
if [ ! -e .env.initrd-flash ]; then
    echo "Missing environment settings" >&2
    exit 1
fi

. .env.initrd-flash

# The .presigning-vars file is generated when binaries
# are signed during the build
PRESIGNED=
if [ -e .presigning-vars ]; then
    . .presigning-vars
    PRESIGNED=yes
fi

usb_instance=
instance_args=
keyfile=
sbk_keyfile=
skip_bootloader=0
qspi_only=0
partition_name=
early_final_status=0
check_usb_instance="${TEGRAFLASH_CHECK_USB_INSTANCE:-no}"
uniflash_flags=""

ARGS=$(getopt -n $(basename "$0") -l "usb-instance:,help,skip-bootloader,external-only,qspi-only,partition,debug" -o "u:v:k:hD" -- "$@")
if [ $? -ne 0 ]; then
    usage >&2
    exit 1
fi
eval set -- "$ARGS"
unset ARGS

while true; do
    case "$1" in
        --usb-instance)
            usb_instance="$2"
            shift 2
            ;;
        --skip-bootloader|--external-only)
            skip_bootloader=1
            shift
            if [ $qspi_only -eq 1 -o -n "$partition_name" ]; then
                echo "ERR: specify only one of --external-only, --qspi-only, --partition" >&2
                exit 1
            fi
            ;;
        --qspi-only)
            qspi_only=1
            shift
            if [ $skip_bootloader -eq 1 -o -n "$partition_name" ]; then
                echo "ERR: specify only one of --external-only, --qspi-only, --partition" >&2
                exit 1
            fi
            ;;
        -u)
            keyfile="$2"
            shift 2
            ;;
        -v)
            sbk_keyfile="$2"
            shift 2
            ;;
        -k|--partition)
            partition_name="$2"
            shift 2
            if [ $skip_bootloader -eq 1 -o $qspi_only -eq 1 ]; then
                echo "ERR: specify only one of --external-only, --qspi-only, --partition" >&2
                exit 1
            fi
            uniflash_flags="$uniflash_flags -u $partition_name"
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        -D|--debug)
            uniflash_flags="$uniflash_flags -D"
            shift
            ;;
        --)
            shift
            break
            ;;
        *)
            echo "Error processing options" >&2
            usage
            exit 1
            ;;
    esac
done

if [ -n "$PRESIGNED" ]; then
    if [ -n "$keyfile" -o -n "$sbk_keyfile" ]; then
        echo "WARN: binaries already signed; ignoring signing options" >&2
        keyfile=
        sbk_keyfile=
    fi
fi

wait_for_rcm() {
    "$here/find-jetson-usb" --wait "$usb_instance"
}

get_board_info() {
    if ! "$here/$FLASH_HELPER" $instance_args --get-board-info 2>&1 >>"$logfile"; then
        echo "ERR: could not retrieve board information" >&2
        exit 1
    fi
    . ./boardvars.sh
    if echo "$CHIP_SKU" | grep -q ":" 2>/dev/null; then
        chip_sku=$(echo "$CHIP_SKU" | cut -d: -f4)
    else
        chip_sku=$CHIP_SKU
    fi
    echo "Board ID($BOARDID) version($FAB) sku($BOARDSKU) revision($BOARDREV) Chip SKU($chip_sku) ramcode($RAMCODE)"
}

prepare_binaries() {
    local target="$1"
    local layout_xml="$2"
    local kernel="$3"
    local rootfs_img="$4"

    if [ "$target" = "internal" ]; then
        if [ -z "$PRESIGNED" ]; then
            if ! MACHINE=$MACHINE BOARDID=$BOARDID FAB=$FAB BOARDSKU=$BOARDSKU BOARDREV=$BOARDREV CHIPREV=$CHIPREV CHIP_SKU=$CHIP_SKU serial_number=$serial_number \
                 "$here/$FLASH_HELPER" --no-flash --sign -u "$keyfile" -v "$sbk_keyfile" $instance_args "$layout_xml" "$kernel" "$rootfs_img"; then
                return 1
            fi
            cp secureflash.xml internal-secureflash.xml
            mv flash.idx internal-flash.idx
        fi
        mkdir -p tools/kernel_flash/images/internal
        if ! stage_files_for_uniflash tools/kernel_flash/images/internal internal-flash.idx internal-secureflash.xml; then
            return 1
        fi
        return 0
    elif [ "$target" = "external" ]; then
        if [ -z "$PRESIGNED" ]; then
            if ! MACHINE=$MACHINE BOARDID=$BOARDID FAB=$FAB BOARDSKU=$BOARDSKU BOARDREV=$BOARDREV CHIPREV=$CHIPREV CHIP_SKU=$CHIP_SKU \
                 "$here/$FLASH_HELPER" --no-flash --sign --external-device -u "$keyfile" -v "$sbk_keyfile" $instance_args "$layout_xml" "$kernel" "$rootfs_img"; then
                return 1
            fi
            mv secureflash.xml external-secureflash.xml
            mv flash.idx external-flash.idx
        fi
        mkdir -p tools/kernel_flash/images/external
        if ! stage_files_for_uniflash tools/kernel_flash/images/external external-flash.idx external-secureflash.xml; then
            return 1
        fi
        return 0
    elif [ "$target" = "rcm-boot" ]; then
        if [ -z "$PRESIGNED" ]; then
            rm -rf rcmboot_blob
            if ! MACHINE=$MACHINE BOARDID=$BOARDID FAB=$FAB BOARDSKU=$BOARDSKU BOARDREV=$BOARDREV CHIPREV=$CHIPREV CHIP_SKU=$CHIP_SKU serial_number=$serial_number \
                 "$here/$FLASH_HELPER" --no-flash --rcm-boot -u "$keyfile" -v "$sbk_keyfile" $instance_args "$layout_xml" "$kernel" "$rootfs_img"; then
                echo "ERR: could not create RCM boot blob" >&2
                return 1
            fi
        fi
        rm -rf bootloader
        mkdir bootloader
        cp -R applet bootloader/
        cp -R rcmboot_blob bootloader/
        echo "$RAMCODE" > bootloader/ramcode.txt
        return 0
    else
        echo "ERR: internal error, unrecognized target: $target" >&2
        return 1
    fi
}

update_flash_cfg_for_partition() {
    local flash_idx_partname="$1"
    local storageline="$2"
    local dest="$3"
    local blksize partnumber partname start_location partsize partfile partguid parttype fstype partfilltoend
    eval "$storageline"
    if [ "$partname" = "$flash_idx_partname" -a -n "$partfile" ]; then
        cp "$partfile" "$dest/"
        echo "${partname}_ext=$partfile" >> "$dest/flash.cfg"
        echo "INFO: staged $dest/$partfile for partition $partname"
    fi
}

stage_files_for_uniflash() {
    local dest="$1"
    local flash_idx="$2"
    local layout_xml="$3"
    local partnumber partloc partname start_location partsize partfile partattrs partsha
    local which=$(basename "$dest")
    local devnum instnum
    local -a partitions
    if [ -n "$layout_xml" ]; then
        mapfile partitions < <("./nvflashxmlparse" -t rootfs "$layout_xml")
    fi
    while IFS=", " read partnumber partloc start_location partsize partfile partattrs partsha; do
        devnum=$(echo "$partloc" | cut -d':' -f 1)
        instnum=$(echo "$partloc" | cut -d':' -f 2)
        partname=$(echo "$partloc" | cut -d':' -f 3)
        if [ -n "$partfile" ]; then
            cp "$partfile" "$dest/" || return 1
        else
            for pline in "${partitions[@]}"; do
                update_flash_cfg_for_partition "$partname" "$pline" "$dest"
            done
        fi
    done < "$flash_idx"
    cp "$flash_idx" "$dest/flash.idx" || return 1
    if [ "$which" = "internal" -a -e flash-upi.idx ]; then
        while IFS=", " read partnumber partloc start_location partsize partfile partattrs partsha; do
            if [ -n "$partfile" ]; then
                cp "$partfile" "$dest/" || return 1
            fi
        done < flash-upi.idx
        cp flash-upi.idx "$dest/" || return 1
    fi
    return 0
}


dtstamp=$(date +"%Y-%m-%d-%H.%M.%S")
logfile="log.initrd-flash.$dtstamp"
stepnumber=1

step_banner() {
    local msg="$1"
    echo "== Step $stepnumber: $msg at $(date -Is) ==" | tee -a "$logfile"
    stepnumber=$(expr $stepnumber \+ 1)
}

echo "Starting at $(date -Is)" | tee "$logfile"
echo "Machine:       ${MACHINE}" | tee "$logfile"
echo "Rootfs device: ${BOOTDEV}" | tee "$logfile"
if ! wait_for_rcm 2>&1 | tee -a "$logfile"; then
    echo "ERR: Device not found at $(date -Is)" | tee -a "$logfile"
    exit 1
fi
if [ -z "$usb_instance" -a -e ".found-jetson" ]; then
    . .found-jetson
fi
if [ -n "$usb_instance" ]; then
    instance_args="--usb-instance $usb_instance"
fi

get_board_info

rm -rf tools/kernel_flash/images

if [ $skip_bootloader -eq 0 ] ; then
    step_banner "Preparing contents for QSPI boot flash"
    if ! prepare_binaries internal flash.xml.in $LNXFILE $ROOTFS_IMAGE 2>&1 >>"$logfile"; then
        echo "ERR: preparing QSPI partitions failed at $(date -Is)"  | tee -a "$logfile"
        exit 1
    fi
fi

if [ $qspi_only -eq 0 ] && [ -e external-flash.xml.in ]; then
    step_banner "Preparing contents for external storage"
    if ! prepare_binaries external external-flash.xml.in $LNXFILE $ROOTFS_IMAGE 2>&1 >>"$logfile"; then
        echo "ERR: preparing external partitions failed at $(date -Is)"  | tee -a "$logfile"
        exit 1
    fi
fi

step_banner "Preparing for RCM boot"
if ! prepare_binaries rcm-boot rcmboot-flash.xml.in initrd-flash.img $ROOTFS_IMAGE 2>&1 >>"$logfile"; then
    echo "ERR: preparing RCM boot blob at $(date -Is)"  | tee -a "$logfile"
    exit 1
fi

step_banner "Setting up unified flash workspace"
export CHIP_SKU
convargs="--profile base"
if [ $qspi_only -eq 0 -a $EXTERNAL_ROOTFS_DRIVE -eq 1 ]; then
    convargs="$convargs --external-device $ROOTFS_DEVICE external-secureflash.xml"
fi
if [ -n "$BOOTSEC_MODE" ]; then
    convargs="$convargs --security-mode $BOOTSEC_MODE"
fi
rm -rf out
mkdir out
./unified_flash/tools/flashtools/bootburn/create_bsp_images.py -b jetson-t264 --toolsonly -l -g $PWD/out --l4t
mkdir -p out/flash_workspace/flash-images out/flash_workspace/rcm-boot
./create_l4t_bsp_images.py $convargs --info --dest $PWD/out
./create_l4t_bsp_images.py $convargs --dest $PWD/out/flash_workspace/flash-images
if [ -n "$partition_name" ]; then
    ./create_l4t_bsp_images.py $convargs -k $partition_name --dest $PWD/out/flash_workspace/flash-images
fi
./create_l4t_bsp_images.py $convargs --dest $PWD/out/flash_workspace/rcm-boot --rcm-boot
cp -R out/flash_workspace/rcm-boot out/flash_workspace/rcm-flash
cat > out/doflash.sh <<EOF
here=\$(readlink -f \$(dirname "\$0"))
oldwd="\$PWD"
"\$here/tools/flashtools/bootburn/flash_bsp_images.py" -b jetson-t264 --l4t -P "\$here/flash_workspace" $instance_args "\$@"
rc=\$?
cd "\$oldwd"
exit \$rc
EOF
chmod +x out/doflash.sh

step_banner "Running unified flash"
./out/doflash.sh $uniflash_flags 2>&1 | tee -a "$logfile"
echo "Finished at $(date -Is)" | tee -a "$logfile"
echo "Host-side log:              $logfile"
exit 0
