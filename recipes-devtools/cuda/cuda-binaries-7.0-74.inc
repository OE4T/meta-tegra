LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/cuda-repo-l4t-7-0-local/copyright;md5=e68945343dffbb690e4ee319f64bd25a"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/003/linux-x64/cuda-repo-l4t-7-0-local_${PV}_arm64.deb"
SRC_URI[md5sum] = "bcb2a99fbd4ef3a5702b11661d908c5c"
SRC_URI[sha256sum] = "c85ca24480d92cd0224286e470db458138715e5d20f656557a0b0c56da4aff70"

S = "${TMPDIR}/work-shared/cuda-binaries-${PV}-${PR}"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
