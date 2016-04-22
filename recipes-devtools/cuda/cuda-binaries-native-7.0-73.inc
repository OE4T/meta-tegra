LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/cuda-repo-ubuntu1404-7-0-local/copyright;md5=e68945343dffbb690e4ee319f64bd25a"

SRC_URI = "http://developer.download.nvidia.com/embedded/L4T/r23_Release_v2.0/CUDA/cuda-repo-ubuntu1404-7-0-local_${PV}_amd64.deb"
SRC_URI[md5sum] = "1ed6585305323745dbad744ec776137d"
SRC_URI[sha256sum] = "db5bded132d64040a4a7905572500c83f9ed43c0d2e7c65315112a2e351b0e2b"

S = "${TMPDIR}/work-shared/cuda-binaries-native-${PV}-${PR}"
B = "${WORKDIR}/build"

inherit native

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
