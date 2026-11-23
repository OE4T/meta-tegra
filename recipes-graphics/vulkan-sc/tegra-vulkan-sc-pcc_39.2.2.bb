SUMMARY = "NVIDIA Vulkan SC offline pipeline compiler"
HOMEPAGE = "https://developer.nvidia.com/embedded/jetpack"
LICENSE = "LicenseRef-Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/nvidia-l4t-vulkan-sc-dev/copyright;md5=7b227641d976dcc6d473fb345610ac57"

COMPATIBLE_MACHINE = "(tegra)"

inherit l4t_deb_pkgfeed features_check

REQUIRED_DISTRO_FEATURES = "vulkan"

SRC_SOC_DEBS = "${@l4t_deb_pkgname(d, 'vulkan-sc-dev')};subdir=${BP};name=main"
SRC_URI[main.sha256sum] = "2dd733c4f8bd2f806c91e53cb32e2e16fe352da68130325e4fa316028d47d69b"

S = "${UNPACKDIR}/${BP}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/usr/bin/pcc ${D}${bindir}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

FILES:${PN} = "${bindir}/pcc"
PACKAGE_ARCH = "${L4T_BSP_PKGARCH}"
