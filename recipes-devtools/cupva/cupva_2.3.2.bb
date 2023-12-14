DESCRIPTION = "Compute Unified Programmable Vision Accelerator SDK"
HOMEPAGE = "https://www.nvidia.com"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/cupva-2.3/copyright;md5=80c94b87ec4eb8b6a9e5235482d332bf"

inherit l4t_deb_pkgfeed

SRC_COMMON_DEBS = "cupva-2.3-l4t_${PV}_arm64.deb;subdir=${BPN}"
SRC_URI[sha256sum] = "b3babc0cf3d029d4a58f6c0eeb366153873b8b18585dba911af5b2909052bcd3"

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
    install -d ${D}/opt/nvidia/cupva-2.3/lib/aarch64-linux-gnu
    install -m 0644 ${B}/opt/nvidia/cupva-2.3/lib/aarch64-linux-gnu/libcupva_host_utils.so.2.3.2 ${D}/opt/nvidia/cupva-2.3/lib/aarch64-linux-gnu
    install -m 0644 ${B}/opt/nvidia/cupva-2.3/lib/aarch64-linux-gnu/libcupva_host.so.2.3.2 ${D}/opt/nvidia/cupva-2.3/lib/aarch64-linux-gnu
    ln -s libcupva_host_utils.so.2.3.2 ${D}/opt/nvidia/cupva-2.3/lib/aarch64-linux-gnu/libcupva_host_utils.so.2.3
    ln -s libcupva_host.so.2.3.2 ${D}/opt/nvidia/cupva-2.3/lib/aarch64-linux-gnu/libcupva_host.so.2.3

    install -d ${D}${sysconfdir}/ld.so.conf.d
    install -m 0644 ${B}/etc/ld.so.conf.d/cupva-2.3-l4t.conf ${D}${sysconfdir}/ld.so.conf.d
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

FILES:${PN} += " \
    /opt/nvidia/cupva-2.3/lib/aarch64-linux-gnu \
    ${sysconfdir}/ld.so.conf.d \
"
RDEPENDS:${PN} += "tegra-libraries-pva tegra-libraries-nvsci tegra-libraries-cuda"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
