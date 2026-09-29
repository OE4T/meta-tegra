L4T_DEB_COPYRIGHT_MD5 = "9108d87525a492087d04b7d93e47f20b"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci"

require tegra-debian-libraries-common.inc

MAINSUM = "e8a94e1102e9f586e4aa5b07a484500154e613bcb9888da8e2e1d6afc7b78cb1"

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
