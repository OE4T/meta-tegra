L4T_DEB_COPYRIGHT_MD5 = "20c2079c67a62b1d526f2a494b57586c"
DEPENDS = "tegra-libraries-eglcore"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-gbm"

require tegra-debian-libraries-common.inc

MAINSUM = "05643d40c44687eec78089936a4f8882ab9f1217f1f2870c4342a1cebf10f367"

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
