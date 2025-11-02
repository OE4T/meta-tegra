L4T_DEB_COPYRIGHT_MD5 = "c5a9810a8ac2bdcdce4e85013d7044d4"
DEPENDS = "tegra-libraries-core tegra-libraries-cuda tegra-libraries-multimedia tegra-libraries-multimedia-utils"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "\
    ${@l4t_deb_pkgname(d, 'multimedia-openrm')};subdir=${BP};name=openrm \
"

MAINSUM = "5eae48eb66eb18833011a2c26adaafbead0116e795a7d103258958ff552e0287"
OPENRMSUM = "b29d917acc6355d4c5c9ea7590e86df19a7305b6c5929e8dd72959aab50398d6"

SRC_URI[openrm.sha256sum] = "${OPENRMSUM}"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvcuvidv4l2.so \
"

TEGRA_PLUGINS = "\
    libv4l2_nvcuvidvideocodec.so \
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
RDEPENDS:${PN} = "tegra-libraries-video-codec"
