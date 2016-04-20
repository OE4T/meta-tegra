LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/cuda-repo-l4t-r23.1-7-0-local/copyright;md5=e68945343dffbb690e4ee319f64bd25a"

SRC_URI = "http://developer.download.nvidia.com/embedded/L4T/r23_Release_v2.0/CUDA/cuda-repo-l4t-r23.1-7-0-local_${PV}_armhf.deb"
SRC_URI[md5sum] = "05756ed10ba887428c141f371e9d13d6"
SRC_URI[sha256sum] = "d152350cf37c742373e5cb30d722ae7a123a5aff7869e3be33fe4cc75cd953d3"

S = "${TMPDIR}/work-shared/cuda-binaries-${PV}-${PR}"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
