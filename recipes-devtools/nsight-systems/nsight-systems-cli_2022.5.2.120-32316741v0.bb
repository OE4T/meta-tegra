DESCRIPTION = "NVIDIA Nsight Systems is a multi-core CPU sampling profiler that \
    provides an interactive view of captured profiling data, helping improve \
    overall application performance."
HOMEPAGE = "https://developer.nvidia.com/nsight-systems"
LICENSE = "Proprietary"
BASE_VERSION = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"
LIC_FILES_CHKSUM = "file://opt/nvidia/nsight-systems/${BASE_VERSION}/EULA.txt;md5=5c45accabbea4eeeb539ce4cd133d5c2"

inherit l4t_deb_pkgfeed

SRC_COMMON_DEBS = "nsight-systems-${BASE_VERSION}_${PV}_arm64.deb;subdir=${BPN}"
SRC_URI[sha256sum] = "9a9b8304ba1fd122dc4d0b46a42efb8475d625bc46dfc6d5ca12bb8bf8e293a3"

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
    install -d ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/
    cp -R --preserve=mode,timestamps,links --no-dereference ${B}/opt/nvidia/nsight-systems/${BASE_VERSION}/target-linux-tegra-armv8/ ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/
    cp -R --preserve=mode,timestamps,links --no-dereference ${B}/opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8/ ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/
    cp -R --preserve=mode,timestamps,links --no-dereference ${B}/opt/nvidia/nsight-systems/${BASE_VERSION}/bin/ ${D}/opt/nvidia/nsight-systems/${BASE_VERSION}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
EXCLUDE_FROM_SHLIBS = "1"

PACKAGES += "${PN}-qdstrmimporter"
FILES:${PN} = " \
    /opt/nvidia/nsight-systems/${BASE_VERSION}/target-linux-tegra-armv8 \
"
FILES:${PN}-qdstrmimporter = " \
    /opt/nvidia/nsight-systems/${BASE_VERSION}/host-linux-armv8 \
    /opt/nvidia/nsight-systems/${BASE_VERSION}/bin \
"
INSANE_SKIP:${PN} = "ldflags file-rdeps"
INSANE_SKIP:${PN}-qdstrmimporter = "ldflags file-rdeps dev-so"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
