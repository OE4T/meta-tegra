LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/cuda-repo-ubuntu1404-7-0-local/copyright;md5=e68945343dffbb690e4ee319f64bd25a"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/004/linux-x64/cuda-repo-ubuntu1404-7-0-local_${PV}_amd64.deb"
SRC_URI[md5sum] = "aef0cb32e45fa60f8f5c9e238758a43c"
SRC_URI[sha256sum] = "a42fb8804c22438a7dca93c60ec3c79578b7f1d1696b7db2911d20ea655e6af1"

S = "${TMPDIR}/work-shared/cuda-binaries-native-${PV}-${PR}"
B = "${WORKDIR}/build"

inherit native

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
