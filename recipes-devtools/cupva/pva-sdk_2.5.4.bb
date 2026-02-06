DESCRIPTION = "Compute Unified Programmable Vision Accelerator SDK"
HOMEPAGE = "https://www.nvidia.com"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/pva-sdk-${BASEVER}/copyright;md5=bb46a5b9fba375d52481253b20200701"

inherit l4t_deb_pkgfeed

BASEVER = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"

SRC_COMMON_DEBS = "pva-sdk-${BASEVER}-l4t_${PV}_arm64.deb;subdir=${BPN}"
SRC_URI[sha256sum] = "7a0b94aee7154753392c3b224ebd30f667046289255a574b760636e63242cb7a"

S = "${UNPACKDIR}/${BPN}"
B = "${S}"

SYSROOT_DIRS:append = " /opt"

COMPATIBLE_MACHINE = "(tegra)"

PROVIDES = "cupva"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}/opt/nvidia/pva-sdk-${BASEVER}/lib/aarch64-linux-gnu
    install -m 0644 ${B}/opt/nvidia/pva-sdk-${BASEVER}/lib/aarch64-linux-gnu/libcupva_host_utils.so.${PV} ${D}/opt/nvidia/pva-sdk-${BASEVER}/lib/aarch64-linux-gnu
    install -m 0644 ${B}/opt/nvidia/pva-sdk-${BASEVER}/lib/aarch64-linux-gnu/libcupva_host.so.${PV} ${D}/opt/nvidia/pva-sdk-${BASEVER}/lib/aarch64-linux-gnu
    ln -s libcupva_host_utils.so.${PV} ${D}/opt/nvidia/pva-sdk-${BASEVER}/lib/aarch64-linux-gnu/libcupva_host_utils.so.${BASEVER}
    ln -s libcupva_host.so.${PV} ${D}/opt/nvidia/pva-sdk-${BASEVER}/lib/aarch64-linux-gnu/libcupva_host.so.${BASEVER}

    install -d ${D}${sysconfdir}/ld.so.conf.d
    install -m 0644 ${B}/etc/ld.so.conf.d/pva-sdk-${BASEVER}-l4t.conf ${D}${sysconfdir}/ld.so.conf.d
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

FILES:${PN} += " \
    /opt/nvidia/pva-sdk-${BASEVER}/lib/aarch64-linux-gnu \
    ${sysconfdir}/ld.so.conf.d \
"
RDEPENDS:${PN} += "tegra-libraries-pva tegra-libraries-nvsci tegra-libraries-cuda"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
