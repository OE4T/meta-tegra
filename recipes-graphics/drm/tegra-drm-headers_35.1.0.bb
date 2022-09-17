DESCRIPTION = "Tegra-specific DRM header file"
HOMEPAGE = "https://developer.nvidia.com/embedded"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://usr/include/libdrm/nvidia/tegra_drm.h;endline=21;md5=5783560983c3ba9365958a674e688a4a"

COMPATIBLE_MACHINE = "(tegra)"

SRC_SOC_DEBS = "nvidia-l4t-weston_${PV}_arm64.deb;subdir=${BPN}"
PV .= "${@l4t_bsp_debian_version_suffix(d)}"

inherit l4t_deb_pkgfeed

SRC_URI[sha256sum] = "50f7d81de6a6b02b4c686e14fb1c7828f6a792f30697c2eeeab6a2fc378796ac"

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
ALLOW_EMPTY:${PN} = "1"
