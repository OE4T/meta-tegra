require tegra-libraries-common.inc

DEPENDS = "libdrm"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvgbm.so \
"

do_install() {
    install_libraries
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RRECOMMENDS_${PN} = "kernel-module-nvgpu"
