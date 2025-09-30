L4T_DEB_COPYRIGHT_MD5 = "67dcaff9ef10899b55a620e5f7e84d8f"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

MAINSUM = "eb229e994cff6d5e5f7b407c32c5eef8eb9fcf33b4b268de5d32508c11acaa97"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libgnarl-24.so \
    nvidia/libgnat-24.so \
"
do_install() {
    install_libraries
	ln -sf libgnarl-24.so ${D}${libdir}/libgnarl.so
	ln -sf libgnat-24.so ${D}${libdir}/libgnat.so
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RPROVIDES:${PN} += "libgnat-24.so()(64bit) libgnarl-24.so()(64bit)"
