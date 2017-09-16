require cuda-binaries-common.inc

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-${CUDA_VERSION}/doc/EULA.txt;md5=cb0e47ada0f0449372d730bff8036853"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/006/linux-x64/cuda-repo-l4t-8-0-local_${PV}_arm64.deb"
SRC_URI[md5sum] = "c1cb4847db2e183499fe8b27f224a19d"
SRC_URI[sha256sum] = "ab7af221a961afcf6b3419c6c1dc220e7c40c0df3ddca0d918e95a18a983a999"

COMPATIBLE_MACHINE = "(jetsontx1)"
