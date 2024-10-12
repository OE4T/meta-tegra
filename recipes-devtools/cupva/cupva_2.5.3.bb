DESCRIPTION = "Compute Unified Programmable Vision Accelerator SDK"
HOMEPAGE = "https://www.nvidia.com"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/cupva-${BASEVER}/copyright;md5=80c94b87ec4eb8b6a9e5235482d332bf"

inherit l4t_deb_pkgfeed

BASEVER = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"

SRC_COMMON_DEBS = "cupva-${BASEVER}-l4t_${PV}_arm64.deb;subdir=${BPN}"
SRC_URI[sha256sum] = "c3729a78a6a03ce97ae988d808f8c1b6f36f1637b6662a22a8f0d5d37f334aca"

S = "${WORKDIR}/${BPN}"
B = "${S}"

SYSROOT_DIRS:append = " /opt"

COMPATIBLE_MACHINE = "(tegra)"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}/opt/nvidia/cupva-${BASEVER}/lib/aarch64-linux-gnu
    install -m 0644 ${B}/opt/nvidia/cupva-${BASEVER}/lib/aarch64-linux-gnu/libcupva_host_utils.so.${PV} ${D}/opt/nvidia/cupva-${BASEVER}/lib/aarch64-linux-gnu
    install -m 0644 ${B}/opt/nvidia/cupva-${BASEVER}/lib/aarch64-linux-gnu/libcupva_host.so.${PV} ${D}/opt/nvidia/cupva-${BASEVER}/lib/aarch64-linux-gnu
    ln -s libcupva_host_utils.so.${PV} ${D}/opt/nvidia/cupva-${BASEVER}/lib/aarch64-linux-gnu/libcupva_host_utils.so.${BASEVER}
    ln -s libcupva_host.so.${PV} ${D}/opt/nvidia/cupva-${BASEVER}/lib/aarch64-linux-gnu/libcupva_host.so.${BASEVER}

    install -d ${D}${sysconfdir}/ld.so.conf.d
    install -m 0644 ${B}/etc/ld.so.conf.d/cupva-${BASEVER}-l4t.conf ${D}${sysconfdir}/ld.so.conf.d
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

FILES:${PN} += " \
    /opt/nvidia/cupva-${BASEVER}/lib/aarch64-linux-gnu \
    ${sysconfdir}/ld.so.conf.d \
"
RDEPENDS:${PN} += "tegra-libraries-pva tegra-libraries-nvsci tegra-libraries-cuda"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
