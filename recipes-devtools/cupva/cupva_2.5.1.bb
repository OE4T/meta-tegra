DESCRIPTION = "Compute Unified Programmable Vision Accelerator SDK"
HOMEPAGE = "https://www.nvidia.com"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/cupva-2.3/copyright;md5=e4e1634d529920e989bc355be47e03be"

inherit l4t_deb_pkgfeed

SRC_COMMON_DEBS = "cupva-2.5-l4t_${PV}_arm64.deb;subdir=${BPN}"
SRC_URI[sha256sum] = "2d16b5189b7ba7f753513f7d12c068633a409a76b712384948de4c88bd8bb9a0"

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
    install -d ${D}/opt/nvidia/cupva-2.5/lib/aarch64-linux-gnu
    install -m 0644 ${B}/opt/nvidia/cupva-2.5/lib/aarch64-linux-gnu/libcupva_host_utils.so.2.5.0 ${D}/opt/nvidia/cupva-2.5/lib/aarch64-linux-gnu
    install -m 0644 ${B}/opt/nvidia/cupva-2.5/lib/aarch64-linux-gnu/libcupva_host.so.2.5.0 ${D}/opt/nvidia/cupva-2.5/lib/aarch64-linux-gnu
    ln -s libcupva_host_utils.so.2.5.0 ${D}/opt/nvidia/cupva-2.5/lib/aarch64-linux-gnu/libcupva_host_utils.so.2.5
    ln -s libcupva_host.so.2.5.0 ${D}/opt/nvidia/cupva-2.5/lib/aarch64-linux-gnu/libcupva_host.so.2.5

    install -d ${D}${sysconfdir}/ld.so.conf.d
    install -m 0644 ${B}/etc/ld.so.conf.d/cupva-2.5-l4t.conf ${D}${sysconfdir}/ld.so.conf.d
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

FILES:${PN} += " \
    /opt/nvidia/cupva-2.5/lib/aarch64-linux-gnu \
    ${sysconfdir}/ld.so.conf.d \
"
RDEPENDS:${PN} += "tegra-libraries-pva tegra-libraries-nvsci tegra-libraries-cuda"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
