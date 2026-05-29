L4T_DEB_COPYRIGHT_MD5 = "67dcaff9ef10899b55a620e5f7e84d8f"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

MAINSUM = "55e59551108c5c3a6d4f5b4b5772e3d1305195ed7d868036524108463aeb4cbb"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libgnarl-25.so \
    nvidia/libgnat-25.so \
"
do_install() {
    install_libraries
	ln -sf libgnarl-25.so ${D}${libdir}/libgnarl.so
	ln -sf libgnat-25.so ${D}${libdir}/libgnat.so
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RPROVIDES:${PN} += "libgnat-25.so()(64bit) libgnarl-25.so()(64bit)"
