L4T_DEB_COPYRIGHT_MD5 = "c5a9810a8ac2bdcdce4e85013d7044d4"
DEPENDS = "tegra-libraries-core tegra-libraries-cuda tegra-libraries-multimedia tegra-libraries-multimedia-utils"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

MAINSUM = "03069358ddda34be5884740d49c0dd8a5154571a0086074376fe1809be4d9d91"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libtegrav4l2.so \
    nvidia/libnvcuvidv4l2.so \
"

TEGRA_PLUGINS = "\
    libv4l2_nvvideocodec.so \
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
