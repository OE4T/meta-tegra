L4T_DEB_COPYRIGHT_MD5 = "c5a9810a8ac2bdcdce4e85013d7044d4"
DEPENDS = "tegra-libraries-core tegra-libraries-cuda tegra-libraries-multimedia tegra-libraries-multimedia-utils"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS:append:tegra264 = " ${@l4t_deb_pkgname(d, 'multimedia-openrm')};subdir=${BP};name=mm"
SRC_SOC_DEBS:append:tegra234 = " ${@l4t_deb_pkgname(d, 'multimedia-nvgpu')};subdir=${BP};name=mm"

MAINSUM = "ea71387feea6c60af41608084c82374596bced0258ac8c40e0de9e2642938ab5"
MULTIMEDIAMSUM:tegra264 = "42f03e5ec0ae5e20cab20d728f2b15f82283caf5b193cf705049bc80eddd916f"
MULTIMEDIAMSUM:tegra234 = "e2d3fc26b6d0d91126623a58e88b10ee3869257fa59226d48b9993e3ef1b0ec4"

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
RDEPENDS:${PN}:append:tegra264 = "tegra-libraries-video-codec"
