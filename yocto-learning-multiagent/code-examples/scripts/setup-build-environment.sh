#!/bin/bash
# Setup Yocto Build Environment for Meta-Tegra
#
# This script automates the setup of a Yocto build environment
# for NVIDIA Jetson platforms using meta-tegra layer.
#
# Usage:
#   ./setup-build-environment.sh [options]
#
# Options:
#   -d, --dir <path>       Build directory (default: build)
#   -m, --machine <name>   Target machine (default: jetson-xavier-nx-devkit)
#   -b, --branch <name>    Yocto branch (default: kirkstone)
#   -h, --help             Show help
#
# Example:
#   ./setup-build-environment.sh -m jetson-orin-nano-devkit -b kirkstone

set -e  # Exit on error

# Configuration
DEFAULT_BUILD_DIR="build"
DEFAULT_MACHINE="jetson-xavier-nx-devkit"
DEFAULT_BRANCH="kirkstone"
YOCTO_REPO="https://git.yoctoproject.org/git/poky"
META_TEGRA_REPO="https://github.com/OE4T/meta-tegra.git"
META_OE_REPO="https://github.com/openembedded/meta-openembedded.git"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

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

print_usage() {
    cat << EOF
Usage: $0 [options]

Setup Yocto build environment for NVIDIA Jetson platforms

Options:
  -d, --dir <path>       Build directory (default: $DEFAULT_BUILD_DIR)
  -m, --machine <name>   Target machine (default: $DEFAULT_MACHINE)
  -b, --branch <name>    Yocto branch (default: $DEFAULT_BRANCH)
  -h, --help             Show this help

Supported machines:
  - jetson-tx2-devkit
  - jetson-tx2-devkit-4gb
  - jetson-xavier-nx-devkit
  - jetson-xavier-nx-devkit-emmc
  - jetson-agx-xavier-devkit
  - jetson-orin-nano-devkit
  - jetson-agx-orin-devkit

Supported branches:
  - kirkstone (recommended)
  - honister
  - hardknott

Example:
  $0 -m jetson-orin-nano-devkit -b kirkstone

EOF
}

check_dependencies() {
    log_info "Checking dependencies..."

    local missing_deps=()

    # Check for required tools
    for tool in git tar python3 gcc g++ make patch diffstat chrpath wget; do
        if ! command -v $tool &> /dev/null; then
            missing_deps+=($tool)
        fi
    done

    if [ ${#missing_deps[@]} -ne 0 ]; then
        log_error "Missing dependencies: ${missing_deps[*]}"
        log_info "Install with: sudo apt-get install ${missing_deps[*]}"
        return 1
    fi

    # Check Python version
    python_version=$(python3 --version | awk '{print $2}')
    if [ "$(printf '%s\n' "3.6" "$python_version" | sort -V | head -n1)" != "3.6" ]; then
        log_error "Python 3.6 or higher required (found $python_version)"
        return 1
    fi

    log_info "All dependencies satisfied"
    return 0
}

clone_or_update_repo() {
    local repo_url=$1
    local dest_dir=$2
    local branch=$3

    if [ -d "$dest_dir/.git" ]; then
        log_info "Updating existing repository: $dest_dir"
        cd "$dest_dir"
        git fetch origin
        git checkout $branch
        git pull
        cd - > /dev/null
    else
        log_info "Cloning repository: $repo_url"
        git clone -b $branch $repo_url $dest_dir
    fi
}

setup_yocto_layers() {
    local branch=$1
    local base_dir=$(pwd)

    log_info "Setting up Yocto layers (branch: $branch)..."

    # Create sources directory
    mkdir -p sources
    cd sources

    # Clone Poky
    clone_or_update_repo "$YOCTO_REPO" "poky" "$branch"

    # Clone meta-tegra
    clone_or_update_repo "$META_TEGRA_REPO" "meta-tegra" "$branch"

    # Clone meta-openembedded
    clone_or_update_repo "$META_OE_REPO" "meta-openembedded" "$branch"

    cd "$base_dir"
}

create_build_directory() {
    local build_dir=$1
    local machine=$2
    local branch=$3

    log_info "Creating build directory: $build_dir"

    # Initialize build environment
    source sources/poky/oe-init-build-env $build_dir

    # Configure bblayers.conf
    log_info "Configuring layers..."

    cat > conf/bblayers.conf << EOF
# LAYER_CONF_VERSION is increased each time build/conf/bblayers.conf
# changes incompatibly
LCONF_VERSION = "7"

BBPATH = "\${TOPDIR}"
BBFILES ?= ""

BBLAYERS ?= " \\
  \${TOPDIR}/../sources/poky/meta \\
  \${TOPDIR}/../sources/poky/meta-poky \\
  \${TOPDIR}/../sources/poky/meta-yocto-bsp \\
  \${TOPDIR}/../sources/meta-openembedded/meta-oe \\
  \${TOPDIR}/../sources/meta-openembedded/meta-python \\
  \${TOPDIR}/../sources/meta-openembedded/meta-networking \\
  \${TOPDIR}/../sources/meta-openembedded/meta-multimedia \\
  \${TOPDIR}/../sources/meta-tegra \\
  "
EOF

    # Configure local.conf
    log_info "Configuring local.conf..."

    cat >> conf/local.conf << EOF

#
# Meta-Tegra Configuration
#
MACHINE = "$machine"

# Parallel build configuration
BB_NUMBER_THREADS ?= "\${@oe.utils.cpu_count()}"
PARALLEL_MAKE ?= "-j \${@oe.utils.cpu_count()}"

# Disk space monitoring
BB_DISKMON_DIRS = "\\
    STOPTASKS,\${TMPDIR},1G,100K \\
    STOPTASKS,\${DL_DIR},1G,100K \\
    STOPTASKS,\${SSTATE_DIR},1G,100K \\
    STOPTASKS,/tmp,100M,100K \\
    HALT,\${TMPDIR},100M,1K \\
    HALT,\${DL_DIR},100M,1K \\
    HALT,\${SSTATE_DIR},100M,1K \\
    HALT,/tmp,10M,1K"

# Download directory (shared between builds)
DL_DIR ?= "\${TOPDIR}/../downloads"

# Shared state cache (for faster rebuilds)
SSTATE_DIR ?= "\${TOPDIR}/../sstate-cache"

# Extra image features
EXTRA_IMAGE_FEATURES ?= "debug-tweaks"

# Accept NVIDIA proprietary license
LICENSE_FLAGS_ACCEPTED = "commercial"

# CUDA and TensorRT versions (adjust as needed)
PREFERRED_VERSION_cuda-toolkit = "11.4%"
PREFERRED_VERSION_tensorrt = "8.4%"
PREFERRED_VERSION_cudnn = "8.4%"

# Package management
PACKAGE_CLASSES ?= "package_deb"

# SDK
SDKMACHINE ?= "x86_64"

# Source mirror (optional)
# INHERIT += "own-mirrors"
# SOURCE_MIRROR_URL ?= "http://example.com/my-mirror"

# BuildHistory (optional but recommended)
INHERIT += "buildhistory"
BUILDHISTORY_COMMIT = "1"

EOF

    log_info "Build environment configured successfully"
}

print_next_steps() {
    local build_dir=$1

    cat << EOF

${GREEN}=================================================================
Build Environment Setup Complete!
=================================================================${NC}

Next steps:

1. Enter build environment:
   ${YELLOW}source sources/poky/oe-init-build-env $build_dir${NC}

2. Build a test image:
   ${YELLOW}bitbake core-image-minimal${NC}

3. Build full featured image:
   ${YELLOW}bitbake core-image-base${NC}

4. Build custom image with CUDA:
   ${YELLOW}bitbake core-image-base${NC}
   Then add to local.conf:
   IMAGE_INSTALL:append = " cuda-toolkit tensorrt"

5. View available recipes:
   ${YELLOW}bitbake-layers show-recipes${NC}

6. Clean specific recipe:
   ${YELLOW}bitbake <recipe> -c cleanall${NC}

7. Generate SDK:
   ${YELLOW}bitbake core-image-base -c populate_sdk${NC}

Build directory: ${GREEN}$build_dir${NC}
Downloads: ${GREEN}downloads${NC}
State cache: ${GREEN}sstate-cache${NC}

For more information:
  - Meta-Tegra: https://github.com/OE4T/meta-tegra
  - Yocto docs: https://docs.yoctoproject.org

EOF
}

# Main script
main() {
    # Parse arguments
    BUILD_DIR="$DEFAULT_BUILD_DIR"
    MACHINE="$DEFAULT_MACHINE"
    BRANCH="$DEFAULT_BRANCH"

    while [[ $# -gt 0 ]]; do
        case $1 in
            -d|--dir)
                BUILD_DIR="$2"
                shift 2
                ;;
            -m|--machine)
                MACHINE="$2"
                shift 2
                ;;
            -b|--branch)
                BRANCH="$2"
                shift 2
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

    log_info "==================================================================="
    log_info "Meta-Tegra Yocto Build Environment Setup"
    log_info "==================================================================="
    log_info "Build directory: $BUILD_DIR"
    log_info "Target machine: $MACHINE"
    log_info "Yocto branch: $BRANCH"
    log_info "==================================================================="
    echo

    # Check dependencies
    if ! check_dependencies; then
        exit 1
    fi

    # Setup layers
    setup_yocto_layers "$BRANCH"

    # Create build directory
    create_build_directory "$BUILD_DIR" "$MACHINE" "$BRANCH"

    # Print next steps
    print_next_steps "$BUILD_DIR"
}

# Run main function
main "$@"
