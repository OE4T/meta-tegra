CUDA_REPO_EXTRA = "-10.0.326"

require cuda-binaries-common.inc

inherit nvidia_devnet_downloads

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-10.0/LICENSE;md5=dfb2d23fe5070ac47b201fbf1e497891 \
	            file://usr/local/cuda-10.0/doc/EULA.txt;md5=049f8f2a31b8c3cc436cc63f0a72c9c9"

SRC_URI = "${NVIDIA_DEVNET_MIRROR}/cuda-repo-l4t-10-0-local-10.0.326_1.0-1_arm64.deb"
SRC_URI[md5sum] = "0e12b2f53c7cbe4233c2da73f7d8e6b4"
SRC_URI[sha256sum] = "3a8bcbd19b102fc4daae6ea00648eae5e65c48e8656e93008072abd37341fd24"
do_unpack[depends] += "xz-native:do_populate_sysroot"

COMPATIBLE_HOST = "(aarch64)"

