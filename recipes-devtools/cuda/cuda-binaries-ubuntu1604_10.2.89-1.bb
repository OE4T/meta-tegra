require cuda-binaries-common.inc

inherit nvidia_devnet_downloads

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-10.2/LICENSE;md5=659d2cacdb888a4ed2aa29014ed0302a \
	            file://usr/local/cuda-10.2/doc/EULA.txt;md5=37774d0b88c5743e8fe8e5c10b057270"

SRC_URI = "${NVIDIA_DEVNET_MIRROR}/cuda-repo-ubuntu1604-10-2-local-10.2.89-440.40_1.0-1_amd64.deb"
SRC_URI[sha256sum] = "e1863bdb86b55d2e1570c6f3cffa5913a242f42a634043226416d3943f2c3753"
do_unpack[depends] += "xz-native:do_populate_sysroot"

COMPATIBLE_HOST = "(x86_64)"
BBCLASSEXTEND = "native nativesdk"
