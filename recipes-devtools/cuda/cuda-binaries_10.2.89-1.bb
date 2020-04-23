CUDA_REPO_EXTRA = "-10.2.89"

require cuda-binaries-common.inc

inherit nvidia_devnet_downloads

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-10.2/LICENSE;md5=659d2cacdb888a4ed2aa29014ed0302a \
	            file://usr/local/cuda-10.2/doc/EULA.txt;md5=37774d0b88c5743e8fe8e5c10b057270"

SRC_URI = "${NVIDIA_DEVNET_MIRROR}/cuda-repo-l4t-10-2-local-10.2.89_1.0-1_arm64.deb"
SRC_URI[sha256sum] = "c29c7e59fd74a785d0756c49159bd7ecc77b8734664a372f8636d86e44bbadab"
do_unpack[depends] += "xz-native:do_populate_sysroot"

COMPATIBLE_HOST = "(aarch64)"

