DEPENDS = "tegra-libraries-core tegra-libraries-eglcore libx11 libxext freetype fontconfig"

require tegra-libraries-common.inc

inherit features_check

REQUIRED_DISTRO_FEATURES = "x11"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libGLX_nvidia.so.0 \
    tegra/libnvidia-glcore.so.${PV} \
"

do_install() {
    install_libraries
}
