do_fetch() {
    :
}
do_fetch[noexec] = "1"
deltask do_unpack
deltask do_patch

SRC_URI = ""
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-8.0/LICENSE;md5=82c8ccff06961d097a50123662b0a89b \
	            file://usr/local/cuda-8.0/doc/EULA.txt;md5=731999c10c8433615a1e9a2b631051f1"
do_configure[depends] += "${MLPREFIX}cuda-binaries:do_preconfigure dpkg-native:do_populate_sysroot"
do_populate_lic[depends] += "${MLPREFIX}cuda-binaries:do_preconfigure"

S = "${TMPDIR}/work-shared/cuda-binaries-${PV}-${PR}"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

INSANE_SKIP_${PN} = "ldflags libdir"

sysroot_stage_dirs_append() {
    sysroot_stage_dir $from${prefix}/local/cuda-8.0 $to${prefix}/local/cuda-8.0
}

COMPATIBLE_MACHINE = "(tegra210|tegra186)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
