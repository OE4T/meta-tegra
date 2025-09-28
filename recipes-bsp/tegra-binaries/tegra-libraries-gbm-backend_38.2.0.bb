L4T_DEB_COPYRIGHT_MD5 = "39ac713283a8a4beb6e0bcb38782e3a1"
DEPENDS = "tegra-libraries-eglcore"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-gbm"

require tegra-debian-libraries-common.inc

MAINSUM = "94f5fa03f8fe8b7f147c23ccce7cf9c85195ae8b5a2af4094b439adb51c21962"

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
