LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/cuda-repo-ubuntu1404-7-0-local/copyright;md5=e68945343dffbb690e4ee319f64bd25a"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/003/linux-x64/cuda-repo-ubuntu1404-7-0-local_${PV}_amd64.deb"
SRC_URI[md5sum] = "67d6128e824167be58c31a78d01dffad"
SRC_URI[sha256sum] = "19b2c8add9690f86e05867244724db4cb01ac78b0d35b2516e26d09bb6bf54aa"

S = "${TMPDIR}/work-shared/cuda-binaries-native-${PV}-${PR}"
B = "${WORKDIR}/build"

inherit native

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
