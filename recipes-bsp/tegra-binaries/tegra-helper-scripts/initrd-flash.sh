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

ARGS=$(getopt -n $(basename "$0") -l "usb-instance:,help,skip-bootloader,external-only,qspi-only,partition" -o "u:v:k:h" -- "$@")
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
            ;;
        -h|--help)
            usage
            exit 0
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

copy_signed_binaries() {
    local signdir="${1:-signed}"
    local xmlfile="${2:-flash.xml.tmp}"
    local destdir="${3:-.}"
    local blksize partnumber partname partsize partfile partguid parttype partfilltoend
    local line

    while read line; do
        eval "$line"
        [ -n "$partfile" ] || continue
        if [ ! -e "$signdir/$partfile" ]; then
            if [ ! -e "$destdir/$partfile" ] && ! echo "$partfile" | grep -q "FILE"; then
                echo "ERR: could not copy $partfile from $signdir" >&2
                return 1
            fi
        else
            cp "$signdir/$partfile" "$destdir"
        fi
    done < <("$here/nvflashxmlparse" -t boot "$signdir/$xmlfile"; "$here/nvflashxmlparse" -t rootfs "$signdir/$xmlfile")
}

get_board_info() {
    if ! "$here/$FLASH_HELPER" $instance_args --get-board-info 2>&1 >>"$logfile"; then
        echo "ERR: could not retrieve borad information" >&2
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

    if [ "$target" = "rcm-boot" ]; then
        rm -rf rcmboot_blob
    fi
    if [ "$target" = "internal" ]; then
        if ! MACHINE=$MACHINE BOARDID=$BOARDID FAB=$FAB BOARDSKU=$BOARDSKU BOARDREV=$BOARDREV CHIPREV=$CHIPREV CHIP_SKU=$CHIP_SKU serial_number=$serial_number \
             "$here/$FLASH_HELPER" --no-flash --sign -u "$keyfile" -v "$sbk_keyfile" $instance_args "$layout_xml" "$kernel" "$rootfs_img"; then
            return 1
        fi
        cp secureflash.xml internal-secureflash.xml
        mkdir -p tools/kernel_flash/images/internal
        if ! stage_files_for_uniflash tools/kernel_flash/images/internal; then
            return 1
        fi
        return 0
    elif [ "$target" = "external" ]; then
        if ! MACHINE=$MACHINE BOARDID=$BOARDID FAB=$FAB BOARDSKU=$BOARDSKU BOARDREV=$BOARDREV CHIPREV=$CHIPREV CHIP_SKU=$CHIP_SKU \
             "$here/$FLASH_HELPER" --no-flash --sign --external-device -u "$keyfile" -v "$sbk_keyfile" $instance_args "$layout_xml" "$kernel" "$rootfs_img"; then
            return 1
        fi
        mv secureflash.xml external-secureflash.xml
        mkdir -p tools/kernel_flash/images/external
        if ! stage_files_for_uniflash tools/kernel_flash/images/external; then
            return 1
        fi
        # XXX need to handle APP_b, APP_ENC, APP_ENC_b
        # XXX better solution might be to munge the flash.idx file, so other partitions get handled too
        $here/mksparse -b 4096 --fillpattern=0 $rootfs_img tools/kernel_flash/images/external/system.img
        echo "APP_ext=system.img" >> tools/kernel_flash/images/external/flash.cfg
        return 0
    elif [ "$target" = "rcm-boot" ]; then
        if ! MACHINE=$MACHINE BOARDID=$BOARDID FAB=$FAB BOARDSKU=$BOARDSKU BOARDREV=$BOARDREV CHIPREV=$CHIPREV CHIP_SKU=$CHIP_SKU serial_number=$serial_number \
             "$here/$FLASH_HELPER" --no-flash --rcm-boot -u "$keyfile" -v "$sbk_keyfile" $instance_args "$layout_xml" "$kernel" "$rootfs_img"; then
            echo "ERR: could not create RCM boot blob" >&2
            return 1
        fi
        rm -rf bootloader
        mkdir bootloader
        mv applet bootloader/
        mv rcmboot_blob bootloader/
        echo "$RAMCODE" > bootloader/ramcode.txt
        return 0
    else
        echo "ERR: internal error, unrecognized target: $target" >&2
        return 1
    fi
}

copy_bootloader_files() {
    local dest="$1"
    local partnumber partloc partname start_location partsize partfile partattrs partsha
    local devnum instnum
    local is_spi is_mmcboot
    rm -f "$dest/partitions.conf"
    while IFS=", " read partnumber partloc start_location partsize partfile partattrs partsha; do
        # Need to trim off leading blanks
        devnum=$(echo "$partloc" | cut -d':' -f 1)
        instnum=$(echo "$partloc" | cut -d':' -f 2)
        partname=$(echo "$partloc" | cut -d':' -f 3)
        # SPI is 3:0
        # eMMC boot blocks (boot0/boot1) are 0:3
        # eMMC user is 1:3
        # NVMe (any external device) is 9:0
        if [ $devnum -eq 3 -a $instnum -eq 0 ] || [ $devnum -eq 0 -a $instnum -eq 3 ]; then
            if [ -n "$partfile" ]; then
                cp "$partfile" "$dest/"
            fi
            if [ $devnum -eq 3 -a $instnum -eq 0 ]; then
                is_spi=yes
            elif [ $devnum -eq 0 -a $instnum -eq 3 ]; then
                is_mmcboot=yes
            fi
            echo "$partname:$start_location:$partsize:$partfile" >> "$dest/partitions.conf"
        fi
    done < flash.idx
    if [ -n "$is_spi" ]; then
        if [ -n "$is_mmcboot" ]; then
            echo "ERR: found bootloader entries for both SPI flash and eMMC boot partitions" >&2
            return 1
        fi
        echo "spi" > "$dest/boot_device_type"
    elif [ -n "$is_mmcboot" ]; then
        echo "mmcboot" > "$dest/boot_device_type"
    else
        echo "ERR: no SPI or eMMC boot partition entries found" >&2
        return 1
    fi
    return 0
}

stage_files_for_uniflash() {
    local dest="$1"
    local partnumber partloc partname start_location partsize partfile partattrs partsha
    local devnum instnum
    while IFS=", " read partnumber partloc start_location partsize partfile partattrs partsha; do
        if [ -n "$partfile" ]; then
            cp "$partfile" "$dest/" || return 1
        fi
    done < flash.idx
    cp flash.idx "$dest/" || return 1
    if [ -e flash-upi.idx ]; then
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
    step_banner "Preparing boot firmware for QSPI"
    if ! prepare_binaries internal flash.xml.in $LNXFILE $ROOTFS_IMAGE 2>&1 >>"$logfile"; then
        echo "ERR: preparing QSPI partitions failed at $(date -Is)"  | tee -a "$logfile"
        exit 1
    fi
fi

if [ $qspi_only -eq 0 ] && [ -e external-flash.xml.in ]; then
    step_banner "Preparing partitions for external storage"
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

if [ -z "$PRESIGNED" ]; then
    [ ! -f ./boardvars.sh ] || . ./boardvars.sh
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
./out/doflash.sh 2>&1 | tee -a "$logfile"
echo "Finished at $(date -Is)" | tee -a "$logfile"
echo "Host-side log:              $logfile"
exit 0
