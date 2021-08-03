DESCRIPTION = "NVIDIA Nsight Systems is a multi-core CPU sampling profiler that \
    provides an interactive view of captured profiling data, helping improve \
    overall application performance."
HOMEPAGE = "https://developer.nvidia.com/nsight-systems"
LICENSE = "Proprietary"
BASE_VERSION = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"
LIC_FILES_CHKSUM = " \
    file://opt/nvidia/${BPN}/${BASE_VERSION}/NVIDIA_SLA.pdf;md5=f7646eea4f29c4f708ea0aa5bc6778a6 \
"

inherit l4t_deb_pkgfeed

SRC_COMMON_DEBS = "${BPN}-${BASE_VERSION}_${PV}-0256620:arm64.deb;subdir=${BPN}"
SRC_URI[sha256sum] = "8a1d1bc76ee4a88793e1df26214e0df9aa69e0b435f9e79acdca84a41e7b3c8f"

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
    cp -R --preserve=mode,timestamps,links --no-dereference ${B}/opt/nvidia/${BPN}/${BASE_VERSION}/target-linux-armv8/ ${D}/opt/nvidia/${BPN}/${BASE_VERSION}/
    cp -R --preserve=mode,timestamps,links --no-dereference ${B}/opt/nvidia/${BPN}/${BASE_VERSION}/host-linux-armv8/ ${D}/opt/nvidia/${BPN}/${BASE_VERSION}/
    cp -R --preserve=mode,timestamps,links --no-dereference ${B}/opt/nvidia/${BPN}/${BASE_VERSION}/bin/ ${D}/opt/nvidia/${BPN}/${BASE_VERSION}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
EXCLUDE_FROM_SHLIBS = "1"

PACKAGES += "${PN}-qdstrmimporter"
FILES:${PN} = " \
    /opt/nvidia/${BPN}/${BASE_VERSION}/target-linux-armv8 \
    /opt/nvidia/${BPN}/${BASE_VERSION}/bin \
"
FILES:${PN}-qdstrmimporter = "/opt/nvidia/${BPN}/${BASE_VERSION}/host-linux-armv8"
INSANE_SKIP:${PN} = "ldflags file-rdeps"
INSANE_SKIP:${PN}-qdstrmimporter = "ldflags file-rdeps"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
