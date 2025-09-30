DESCRIPTION = "Tegra-specific DRM header file"
HOMEPAGE = "https://developer.nvidia.com/embedded"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://usr/include/libdrm/nvidia/tegra_drm.h;endline=21;md5=5783560983c3ba9365958a674e688a4a"

COMPATIBLE_MACHINE = "(tegra)"

SRC_SOC_DEBS = "${@l4t_deb_pkgname(d, 'weston')};subdir=${BPN}"
PV .= "${@l4t_bsp_debian_version_suffix(d, pkgname='nvidia-l4t-weston')}"

inherit l4t_deb_pkgfeed

SRC_URI[sha256sum] = "8cc2522c2c47e30ffef2d69c313ccf7569c7d9825b04bc023da1e43ae77bb0a2"

S = "${UNPACKDIR}/${BPN}"
B = "${S}"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}${includedir}/libdrm/nvidia
    install -m 0644 ${S}/usr/include/libdrm/nvidia/tegra_drm.h ${D}${includedir}/libdrm/nvidia/
}

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
ALLOW_EMPTY:${PN} = "1"
