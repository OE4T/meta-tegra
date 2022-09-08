L4T_DEB_COPYRIGHT_MD5 = "20c2079c67a62b1d526f2a494b57586c"
DEPENDS = "tegra-libraries-gbm tegra-libraries-eglcore"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-gbm"

require tegra-debian-libraries-common.inc

MAINSUM = "b5fe862241eeac1535e7e658c04946c2c02fd192e69361378b543ae1d9c43065"

RPROVIDES:${PN} += "tegra-gbm-backend"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvidia-allocator.so.1 \
"

do_install() {
    install_libraries

    install -d ${D}${libdir}/gbm
    ln -sf ../libnvidia-allocator.so ${D}${libdir}/gbm/tegra-udrm_gbm.so
    ln -sf ../libnvidia-allocator.so ${D}${libdir}/gbm/nvidia-drm_gbm.so
    ln -sf ../libnvidia-allocator.so ${D}${libdir}/gbm/tegra_gbm.so
    ln -sf libnvidia-allocator.so.1 ${D}${libdir}/libnvidia-allocator.so
}

FILES:${PN} += " \
    ${libdir}/gbm \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
