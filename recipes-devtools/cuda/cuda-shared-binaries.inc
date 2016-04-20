do_fetch() {
    :
}
do_fetch[noexec] = "1"
deltask do_unpack
deltask do_patch

SRC_URI = ""
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-7.0/LICENSE;md5=dbd6ede839a756f0a73fb42a8b0e5891 \
	            file://usr/local/cuda-7.0/doc/EULA.txt;md5=d4210c281d303b080c3b835752cba174"
do_configure[depends] += "${MLPREFIX}cuda-binaries:do_preconfigure dpkg-native:do_populate_sysroot"
do_populate_lic[depends] += "${MLPREFIX}cuda-binaries:do_preconfigure"

S = "${TMPDIR}/work-shared/cuda-binaries-${PV}-${PR}"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

INSANE_SKIP_${PN} = "ldflags libdir"

sysroot_stage_dirs_append() {
    sysroot_stage_dir $from${prefix}/local/cuda-7.0 $to${prefix}/local/cuda-7.0
}
