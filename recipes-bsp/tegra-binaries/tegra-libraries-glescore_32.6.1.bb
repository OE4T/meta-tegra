DEPENDS = "tegra-libraries-core tegra-libraries-eglcore"

require tegra-libraries-common.inc

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra-egl/libGLESv1_CM_nvidia.so.1 \
    tegra-egl/libGLESv2_nvidia.so.2 \
"    

do_install() {
    install_libraries
}
