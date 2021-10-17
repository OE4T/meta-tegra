DEPENDS = "tegra-libraries-core tegra-libraries-multimedia alsa-lib"

require tegra-libraries-common.inc

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvomx.so \
    tegra/libnvomxilclient.so \
"

do_install() {
  install_libraries
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
