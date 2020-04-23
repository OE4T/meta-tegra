require cuda-binaries-common.inc

inherit nvidia_devnet_downloads

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-10.2/LICENSE;md5=659d2cacdb888a4ed2aa29014ed0302a \
	            file://usr/local/cuda-10.2/doc/EULA.txt;md5=37774d0b88c5743e8fe8e5c10b057270"

SRC_URI = "${NVIDIA_DEVNET_MIRROR}/cuda-repo-ubuntu1804-10-2-local-10.2.89-440.40_1.0-1_amd64.deb"
SRC_URI[sha256sum] = "0a24f326161e2dfec2051b88f476f34b5f085264f952df9d3ef54800a915f3cf"
do_unpack[depends] += "xz-native:do_populate_sysroot"

COMPATIBLE_HOST = "(x86_64)"
BBCLASSEXTEND = "native nativesdk"
