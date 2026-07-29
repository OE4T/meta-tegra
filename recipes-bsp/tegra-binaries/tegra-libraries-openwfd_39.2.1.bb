L4T_DEB_COPYRIGHT_MD5 = "9108d87525a492087d04b7d93e47f20b"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci"

require tegra-debian-libraries-common.inc

MAINSUM = "964d96d8dfd076024db3bea298896286ba76a53de31cc1c2009c187d46628d3d"

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
