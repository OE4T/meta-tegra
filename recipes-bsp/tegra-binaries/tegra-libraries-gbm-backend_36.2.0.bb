L4T_DEB_COPYRIGHT_MD5 = "1be94c30c7fd74260dc698823f45e81d"
DEPENDS = "tegra-libraries-eglcore"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-gbm"

require tegra-debian-libraries-common.inc

MAINSUM = "77673844fff3027a0fa3448bdd5502ca1b4f46a95eae6cd49a78c92895116c8e"

RPROVIDES:${PN} += "tegra-gbm-backend"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvidia-allocator.so.1 \
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
