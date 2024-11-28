DESCRIPTION = "Tegra-specific DRM header file"
HOMEPAGE = "https://developer.nvidia.com/embedded"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://usr/include/libdrm/nvidia/tegra_drm.h;endline=21;md5=5783560983c3ba9365958a674e688a4a"

COMPATIBLE_MACHINE = "(tegra)"

# The sources for t186 and t210 are identical, so just pick one
L4T_DEB_SOCNAME = "t186"
L4T_BSP_DEB_VERSION = "${L4T_BSP_DEB_DEFAULT_VERSION_T186}"
SRC_SOC_DEBS = "nvidia-l4t-weston_${PV}_arm64.deb;subdir=${BPN}"
PV .= "${@l4t_bsp_debian_version_suffix(d)}"

inherit l4t_deb_pkgfeed

SRC_URI[sha256sum] = "e6fea7ac58260f6da0325988db35cba169dfd8eadccade1f6adb5d570f49d207"

S = "${WORKDIR}/${BPN}"
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
ALLOW_EMPTY_${PN} = "1"
