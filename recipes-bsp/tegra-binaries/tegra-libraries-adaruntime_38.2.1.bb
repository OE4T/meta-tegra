L4T_DEB_COPYRIGHT_MD5 = "67dcaff9ef10899b55a620e5f7e84d8f"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

MAINSUM = "a4f872d8bda41388d186db70dee80939e95943d79084dcf8929b341223debe4b"

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
