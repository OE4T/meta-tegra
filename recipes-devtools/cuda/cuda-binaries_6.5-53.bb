require cuda-binaries-common.inc

CUDA_VERSION ?= "6.5"

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-${CUDA_VERSION}/doc/EULA.txt;md5=035ceb64718e9de387e31cd635e782a9"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/005/linux-x64/cuda-repo-l4t-r21.5-6-5-local_${PV}_armhf.deb"
SRC_URI[md5sum] = "93b5c6592292565c389c46a16b4cbf5d"
SRC_URI[sha256sum] = "4739c63e16b9a169f988c2b8f175c0182fa118833b49950fe72fe8c039ba1359"

COMPATIBLE_MACHINE = "(tegra124)"
