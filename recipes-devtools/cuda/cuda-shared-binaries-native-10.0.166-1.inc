do_fetch() {
    :
}
do_fetch[noexec] = "1"
deltask do_unpack
deltask do_patch

SRC_URI = ""
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-10.0/LICENSE;md5=dfb2d23fe5070ac47b201fbf1e497891 \
	            file://usr/local/cuda-10.0/doc/EULA.txt;md5=987e5f80d382a9feae03e4afd8855dea"
do_configure[depends] += "cuda-binaries-native:do_preconfigure dpkg-native:do_populate_sysroot"
do_populate_lic[depends] += "cuda-binaries-native:do_preconfigure"

S = "${TMPDIR}/work-shared/cuda-binaries-native-${PV}-${PR}"
B = "${WORKDIR}/build"

inherit native

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

sysroot_stage_dirs_append() {
    sysroot_stage_dir $from${prefix}/local/cuda-10.0 $to${prefix}/local/cuda-10.0
}

PR = "r0"
