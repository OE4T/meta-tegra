L4T_DEB_COPYRIGHT_MD5 = "9108d87525a492087d04b7d93e47f20b"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci"

require tegra-debian-libraries-common.inc

MAINSUM = "7d03fdcc13a0d4014c6c4bfcf7ba02158d301f76e6f8ce535026626b4205f12e"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libtegrawfd.so \
    nvidia/libnvidia-kms.so.${L4T_LIB_VERSION} \
"
do_install() {
    install_libraries
    ln -sf libnvidia-kms.so.${L4T_LIB_VERSION} ${D}${libdir}/libnvidia-kms.so
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RPROVIDES:${PN} += "libnvidia-kms.so()(64bit)"
