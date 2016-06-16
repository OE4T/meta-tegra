LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/cuda-repo-l4t-7-0-local/copyright;md5=e68945343dffbb690e4ee319f64bd25a"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/004/linux-x64/cuda-repo-l4t-7-0-local_${PV}_arm64.deb"
SRC_URI[md5sum] = "96800e3237340492cc14518f944d66f0"
SRC_URI[sha256sum] = "8c7b79dcae1ffc527dbf538c80fe131205b2e857ffde3ef91aa077e4d8734e20"

S = "${TMPDIR}/work-shared/cuda-binaries-${PV}-${PR}"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
