DESCRIPTION = "Tegra-specific DRM header file"
HOMEPAGE = "https://developer.nvidia.com/embedded"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://usr/include/libdrm/nvidia/tegra_drm.h;endline=21;md5=5783560983c3ba9365958a674e688a4a"

COMPATIBLE_MACHINE = "(tegra)"

SRC_SOC_DEBS = "nvidia-l4t-weston_${PV}_arm64.deb;subdir=${BPN}"
PV .= "${@l4t_bsp_debian_version_suffix(d)}"

inherit l4t_deb_pkgfeed

<<<<<<<< HEAD:recipes-graphics/drm/tegra-drm-headers_32.7.2.bb
SRC_URI[sha256sum] = "5548e889725cb020426894d8043ea5c117e23b894c400adaa999d3c30704388c"
========
SRC_URI[sha256sum] = "d2470e149573158c1230ce89693e2efb397f228df754eee77fc72410a6102aad"
>>>>>>>> 4bce7a7b (tegra-drm-headers: update for 34.1.0):recipes-graphics/drm/tegra-drm-headers_34.1.0.bb

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
