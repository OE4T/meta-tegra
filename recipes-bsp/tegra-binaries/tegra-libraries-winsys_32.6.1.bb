require tegra-libraries-common.inc

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvwinsys.so \
"

do_install() {
    install_libraries
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
