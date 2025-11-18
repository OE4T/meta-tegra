#!/bin/bash
# Flash NVIDIA Jetson Device with Yocto Image
#
# This script simplifies flashing Jetson devices with images
# built using Yocto and meta-tegra.
#
# Usage:
#   sudo ./flash-jetson.sh [options]
#
# Options:
#   -m, --machine <name>   Target machine (required)
#   -i, --image <path>     Image directory (default: auto-detect)
#   -d, --device <dev>     Target device (default: mmcblk0p1)
#   -s, --storage <type>   Storage type: emmc, nvme, sd, usb (default: emmc)
#   --skip-verify          Skip image verification
#   --force                Force flash without confirmation
#   -h, --help             Show help
#
# Example:
#   sudo ./flash-jetson.sh -m jetson-xavier-nx-devkit

set -e  # Exit on error

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Default values
DEFAULT_STORAGE="emmc"
DEFAULT_DEVICE="mmcblk0p1"
SKIP_VERIFY=0
FORCE=0

# Functions
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

print_usage() {
    cat << EOF
Usage: $0 [options]

Flash NVIDIA Jetson device with Yocto-built image

Options:
  -m, --machine <name>   Target machine (required)
  -i, --image <path>     Image directory (default: auto-detect from build)
  -d, --device <dev>     Target device (default: $DEFAULT_DEVICE)
  -s, --storage <type>   Storage type: emmc, nvme, sd, usb (default: $DEFAULT_STORAGE)
  --skip-verify          Skip image verification
  --force                Force flash without confirmation
  -h, --help             Show this help

Supported machines:
  - jetson-tx2-devkit
  - jetson-xavier-nx-devkit
  - jetson-xavier-nx-devkit-emmc
  - jetson-agx-xavier-devkit
  - jetson-orin-nano-devkit
  - jetson-agx-orin-devkit

Storage types:
  - emmc:  Internal eMMC storage
  - nvme:  NVMe SSD
  - sd:    SD card
  - usb:   USB storage

Prerequisites:
  1. Jetson device in recovery mode
  2. USB connection to host
  3. Run as root (sudo)

Recovery mode:
  1. Power off the device
  2. Hold RECOVERY button
  3. Press and release POWER button
  4. Hold RECOVERY for 2 more seconds
  5. Connect USB cable to host

Verify recovery mode:
  lsusb | grep -i nvidia

Example:
  sudo $0 -m jetson-xavier-nx-devkit -s emmc

EOF
}

check_root() {
    if [ "$EUID" -ne 0 ]; then
        log_error "This script must be run as root (use sudo)"
        exit 1
    fi
}

check_recovery_mode() {
    log_step "Checking if device is in recovery mode..."

    if lsusb | grep -qi "nvidia"; then
        log_info "Jetson device detected in recovery mode"
        lsusb | grep -i nvidia
        return 0
    else
        log_error "No Jetson device found in recovery mode"
        log_info "Please put device in recovery mode and try again"
        log_info "See --help for recovery mode instructions"
        return 1
    fi
}

find_image_directory() {
    local machine=$1
    local image_path=""

    log_step "Searching for image directory..."

    # Common image locations
    local search_paths=(
        "build/tmp/deploy/images/$machine"
        "tmp/deploy/images/$machine"
        "../build/tmp/deploy/images/$machine"
        "$PWD/build/tmp/deploy/images/$machine"
    )

    for path in "${search_paths[@]}"; do
        if [ -d "$path" ]; then
            image_path="$path"
            log_info "Found image directory: $image_path"
            break
        fi
    done

    if [ -z "$image_path" ]; then
        log_error "Could not find image directory for machine: $machine"
        log_info "Please specify image directory with -i option"
        return 1
    fi

    echo "$image_path"
}

verify_image() {
    local image_dir=$1

    log_step "Verifying image files..."

    # Check for required files
    local required_files=(
        "*.tegraflash.tar.gz"
    )

    for pattern in "${required_files[@]}"; do
        if ! ls $image_dir/$pattern 1> /dev/null 2>&1; then
            log_warn "Missing file: $pattern"
        fi
    done

    # List available images
    log_info "Available images in $image_dir:"
    ls -lh $image_dir/*.tegraflash.tar.gz 2>/dev/null || true
    ls -lh $image_dir/*.ext4 2>/dev/null || true
}

extract_tegraflash() {
    local image_dir=$1
    local work_dir=$2

    log_step "Extracting tegraflash package..."

    # Find tegraflash archive
    local tegraflash_archive=$(ls $image_dir/*.tegraflash.tar.gz 2>/dev/null | head -n 1)

    if [ -z "$tegraflash_archive" ]; then
        log_error "No tegraflash archive found in $image_dir"
        return 1
    fi

    log_info "Using: $tegraflash_archive"

    # Extract to work directory
    mkdir -p $work_dir
    tar -xzf $tegraflash_archive -C $work_dir

    log_info "Extracted to: $work_dir"
}

get_flash_command() {
    local machine=$1
    local storage=$2
    local device=$3

    # Map storage type to flash target
    case $storage in
        emmc)
            flash_target="$device"
            ;;
        nvme)
            flash_target="nvme0n1p1"
            ;;
        sd)
            flash_target="mmcblk1p1"
            ;;
        usb)
            flash_target="sda1"
            ;;
        *)
            flash_target="$device"
            ;;
    esac

    # Get flash script name based on machine
    local flash_script="flash.sh"

    echo "./doflash.sh $flash_target"
}

flash_device() {
    local work_dir=$1
    local machine=$2
    local storage=$3
    local device=$4

    log_step "Starting flash process..."

    cd $work_dir

    # Make flash script executable
    chmod +x doflash.sh

    # Get flash command
    local flash_cmd=$(get_flash_command "$machine" "$storage" "$device")

    log_info "Flash command: $flash_cmd"

    if [ $FORCE -eq 0 ]; then
        echo
        log_warn "This will erase all data on the target device!"
        read -p "Continue with flashing? (yes/no): " confirm

        if [ "$confirm" != "yes" ]; then
            log_info "Flash cancelled by user"
            return 1
        fi
    fi

    log_info "Flashing device... (this may take several minutes)"

    # Execute flash command
    if $flash_cmd; then
        log_info "Flash completed successfully!"
        return 0
    else
        log_error "Flash failed!"
        return 1
    fi
}

cleanup() {
    local work_dir=$1

    if [ -d "$work_dir" ]; then
        log_info "Cleaning up temporary files..."
        rm -rf "$work_dir"
    fi
}

print_post_flash_instructions() {
    cat << EOF

${GREEN}=================================================================
Flash Complete!
=================================================================${NC}

Next steps:

1. Disconnect USB cable from Jetson
2. Power cycle the device
3. Device should boot into the flashed image

First boot setup:
  - Default username: root (if debug-tweaks enabled)
  - No password (if debug-tweaks enabled)
  - For production images, see image documentation

Troubleshooting:
  - Device not booting: Check serial console output
  - Black screen: Wait 1-2 minutes for first boot
  - Network issues: Check Ethernet/WiFi configuration

Serial console:
  - Baud rate: 115200
  - Data bits: 8
  - Parity: None
  - Stop bits: 1
  - Flow control: None

  Connect with: sudo screen /dev/ttyUSB0 115200
  Or: sudo minicom -D /dev/ttyUSB0 -b 115200

For more information:
  - Meta-Tegra documentation
  - NVIDIA L4T documentation
  - Yocto Project documentation

EOF
}

# Main script
main() {
    # Parse arguments
    MACHINE=""
    IMAGE_DIR=""
    DEVICE="$DEFAULT_DEVICE"
    STORAGE="$DEFAULT_STORAGE"

    while [[ $# -gt 0 ]]; do
        case $1 in
            -m|--machine)
                MACHINE="$2"
                shift 2
                ;;
            -i|--image)
                IMAGE_DIR="$2"
                shift 2
                ;;
            -d|--device)
                DEVICE="$2"
                shift 2
                ;;
            -s|--storage)
                STORAGE="$2"
                shift 2
                ;;
            --skip-verify)
                SKIP_VERIFY=1
                shift
                ;;
            --force)
                FORCE=1
                shift
                ;;
            -h|--help)
                print_usage
                exit 0
                ;;
            *)
                log_error "Unknown option: $1"
                print_usage
                exit 1
                ;;
        esac
    done

    # Validate arguments
    if [ -z "$MACHINE" ]; then
        log_error "Machine name is required (-m option)"
        print_usage
        exit 1
    fi

    log_info "==================================================================="
    log_info "Jetson Flash Script"
    log_info "==================================================================="
    log_info "Target machine: $MACHINE"
    log_info "Storage type: $STORAGE"
    log_info "Target device: $DEVICE"
    log_info "==================================================================="
    echo

    # Check root
    check_root

    # Check recovery mode
    if ! check_recovery_mode; then
        exit 1
    fi

    # Find image directory
    if [ -z "$IMAGE_DIR" ]; then
        IMAGE_DIR=$(find_image_directory "$MACHINE")
        if [ $? -ne 0 ]; then
            exit 1
        fi
    fi

    # Verify image
    if [ $SKIP_VERIFY -eq 0 ]; then
        verify_image "$IMAGE_DIR"
    fi

    # Create temporary work directory
    WORK_DIR=$(mktemp -d -t jetson-flash-XXXXXX)
    trap "cleanup $WORK_DIR" EXIT

    # Extract tegraflash package
    if ! extract_tegraflash "$IMAGE_DIR" "$WORK_DIR"; then
        exit 1
    fi

    # Flash device
    if flash_device "$WORK_DIR" "$MACHINE" "$STORAGE" "$DEVICE"; then
        print_post_flash_instructions
        exit 0
    else
        exit 1
    fi
}

# Run main function
main "$@"
