L4T_DEB_COPYRIGHT_MD5 = "67dcaff9ef10899b55a620e5f7e84d8f"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

MAINSUM = "df5c996d980e6383eb2c4e545fb923d4153f7e4af19a4fd022a9cfa7cc1c253e"

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
