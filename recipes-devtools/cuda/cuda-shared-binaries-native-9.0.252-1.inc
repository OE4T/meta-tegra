do_fetch() {
    :
}
do_fetch[noexec] = "1"
deltask do_unpack
deltask do_patch

SRC_URI = ""
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-9.0/LICENSE;md5=d87e5ceb8a41dd716b3205c4e00aa729 \
	            file://usr/local/cuda-9.0/doc/EULA.txt;md5=1d9340fbe1f77282520c3ef05235c26a"
do_configure[depends] += "cuda-binaries-native:do_preconfigure dpkg-native:do_populate_sysroot"
do_populate_lic[depends] += "cuda-binaries-native:do_preconfigure"

PR = "rc0"

S = "${TMPDIR}/work-shared/cuda-binaries-native-${PV}-${PR}"
B = "${WORKDIR}/build"

inherit native

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

sysroot_stage_dirs_append() {
    sysroot_stage_dir $from${prefix}/local/cuda-9.0 $to${prefix}/local/cuda-9.0
}

PR = "rc0"
