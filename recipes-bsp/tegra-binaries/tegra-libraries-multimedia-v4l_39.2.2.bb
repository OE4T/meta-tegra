L4T_DEB_COPYRIGHT_MD5 = "c5a9810a8ac2bdcdce4e85013d7044d4"
DEPENDS = "tegra-libraries-core tegra-libraries-cuda tegra-libraries-multimedia tegra-libraries-multimedia-utils"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS:append:tegra264 = " ${@l4t_deb_pkgname(d, 'multimedia-openrm')};subdir=${BP};name=mm"
SRC_SOC_DEBS:append:tegra234 = " ${@l4t_deb_pkgname(d, 'multimedia-nvgpu')};subdir=${BP};name=mm"

MAINSUM = "d43aff2a973f1f3936c8f7b41dc18b1a483f87e27c96adbd2cc05754ea8aca49"
MULTIMEDIAMSUM:tegra264 = "efde0f36e8a0dee6bd8370f0ffa96972e2afccd507655b233eb1a1a30a78752f"
MULTIMEDIAMSUM:tegra234 = "3fb838952d92f955bb655eaa4985769fe9ed728a7e0952d520524b862c48c8ae"

SRC_URI[mm.sha256sum] = "${MULTIMEDIAMSUM}"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = ""
TEGRA_LIBRARIES_TO_INSTALL:append:tegra264 = "\
    nvidia/libnvcuvidv4l2.so \
"
TEGRA_LIBRARIES_TO_INSTALL:append:tegra234 = "\
    nvidia/libtegrav4l2.so \
"

TEGRA_PLUGINS = ""
TEGRA_PLUGINS:append:tegra264 = "\
    libv4l2_nvcuvidvideocodec.so \
"
TEGRA_PLUGINS:append:tegra234 = "\
    libv4l2_nvvideocodec.so \
"

do_install() {
    install_libraries
    install -d ${D}${libdir}/libv4l/plugins
    for f in ${TEGRA_PLUGINS}; do
        install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/nvidia/$f ${D}${libdir}/libv4l/plugins/
    done
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
FILES:${PN} += "${libdir}/libv4l"
EXTRA_RDEPS = ""
EXTRA_RDEPS:tegra264 = "tegra-libraries-video-codec"
RDEPENDS:${PN} = "${EXTRA_RDEPS}"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
