DESCRIPTION = "NVIDIA Nsight Systems is a multi-core CPU sampling profiler that \
    provides an interactive view of captured profiling data, helping improve \
    overall application performance."
HOMEPAGE = "https://developer.nvidia.com/nsight-systems"
LICENSE = "Proprietary"
BASE_VERSION = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"
LIC_FILES_CHKSUM = "file://opt/nvidia/nsight-systems/${BASE_VERSION}/EULA.txt;md5=59a793e0da68faeaa65ccfa38d9408b4"

inherit l4t_deb_pkgfeed

SRC_COMMON_DEBS = "nsight-systems-${BASE_VERSION}_${PV}_arm64.deb;subdir=${BPN}"
SRC_URI[sha256sum] = "404b1d921366d94f60a027298523e295c5484ee6f6e3b8a27da304ad4fd92bad"

S = "${UNPACKDIR}/${BPN}"
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
INSANE_SKIP:${PN} = "ldflags file-rdeps dev-so"
INSANE_SKIP:${PN}-qdstrmimporter = "ldflags file-rdeps dev-so"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
