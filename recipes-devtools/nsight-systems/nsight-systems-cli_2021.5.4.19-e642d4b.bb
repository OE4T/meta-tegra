DESCRIPTION = "NVIDIA Nsight Systems is a multi-core CPU sampling profiler that \
    provides an interactive view of captured profiling data, helping improve \
    overall application performance."
HOMEPAGE = "https://developer.nvidia.com/nsight-systems"
LICENSE = "Proprietary"
BASE_VERSION = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"
LIC_FILES_CHKSUM = "file://opt/nvidia/${BPN}/${BASE_VERSION}/EULA.txt;md5=5c45accabbea4eeeb539ce4cd133d5c2"

inherit l4t_deb_pkgfeed

SRC_COMMON_DEBS = "nsight-systems-linux-tegra-public-${PV}_${PV}_arm64.deb;subdir=${BPN}"
SRC_URI[sha256sum] = "fc18051c54c631819558d894cdb71497aba17972e5528d48f718c5ea65d3ab90"

S = "${WORKDIR}/${BPN}"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}/opt/nvidia/${BPN}/${BASE_VERSION}/
    cp -R --preserve=mode,timestamps,links --no-dereference ${B}/opt/nvidia/${BPN}/${BASE_VERSION}/target-linux-tegra-armv8/ ${D}/opt/nvidia/${BPN}/${BASE_VERSION}/
    cp -R --preserve=mode,timestamps,links --no-dereference ${B}/opt/nvidia/${BPN}/${BASE_VERSION}/host-linux-armv8/ ${D}/opt/nvidia/${BPN}/${BASE_VERSION}/
    cp -R --preserve=mode,timestamps,links --no-dereference ${B}/opt/nvidia/${BPN}/${BASE_VERSION}/bin/ ${D}/opt/nvidia/${BPN}/${BASE_VERSION}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
EXCLUDE_FROM_SHLIBS = "1"

PACKAGES += "${PN}-qdstrmimporter"
FILES:${PN} = " \
    /opt/nvidia/${BPN}/${BASE_VERSION}/target-linux-tegra-armv8 \
    /opt/nvidia/${BPN}/${BASE_VERSION}/bin \
"
FILES:${PN}-qdstrmimporter = "/opt/nvidia/${BPN}/${BASE_VERSION}/host-linux-armv8"
INSANE_SKIP:${PN} = "ldflags file-rdeps"
INSANE_SKIP:${PN}-qdstrmimporter = "ldflags file-rdeps"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
