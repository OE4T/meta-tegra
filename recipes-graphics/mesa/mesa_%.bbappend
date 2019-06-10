FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += "file://egl-gles2-nv-extensions.patch"

DEPENDS += "tegra-mmapi-glheaders"

PACKAGECONFIG[glvnd] = "--enable-libglvnd,--disable-libglvnd,libglvnd"

PACKAGECONFIG_append_tegra186 = " glvnd"
PACKAGECONFIG_append_tegra194 = " glvnd"
PACKAGECONFIG_append_tegra210 = " glvnd"
DEPENDS_append_tegra = " tegra-libraries"
EXTRA_OECONF_append_tegra = " --without-dri-drivers --disable-dri3"

python () {
    overrides = d.getVar("OVERRIDES").split(":")
    if "tegra" not in overrides:
        return

    x11flag = d.getVarFlag("PACKAGECONFIG", "x11", False)
    d.setVarFlag("PACKAGECONFIG", "x11", x11flag.replace("--enable-glx-tls", "--enable-glx"))
}

move_libraries() {
    install -d ${D}${libdir}/mesa
    if [ -e ${D}${libdir}/libGL.so ]; then
       mv ${D}${libdir}/libGL.* ${D}${libdir}/mesa/
    fi
    if [ -e ${D}${libdir}/libGLESv2.so ]; then
        mv ${D}${libdir}/libGLES* ${D}${libdir}/mesa/
    fi
    if [ -e ${D}${libdir}/libEGL.so ]; then
        mv ${D}${libdir}/libEGL.* ${D}${libdir}/mesa/
    fi
}

do_install_append() {
    if ${@bb.utils.contains("PACKAGECONFIG", "glvnd", "true", "false", d)}; then
        rm -f ${D}${libdir}/libGLESv1_CM.so* ${D}${libdir}/libGLESv2.so*
        if [ -e ${D}${libdir}/pkgconfig/gl.pc ]; then
	    sed -i -e's,lGLX_mesa,lGL,' ${D}${libdir}/pkgconfig/gl.pc
        fi
    fi
}

do_install_append_tegra() {
    if ${@bb.utils.contains("PACKAGECONFIG", "glvnd", "false", "true", d)}; then
        move_libraries
    fi
}

PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"
FILES_libegl-mesa += "${libdir}/libEGL_mesa.so.* ${datadir}/glvnd"
FILES_libegl-mesa-dev += "${libdir}/libEGL_mesa.so"
FILES_libgl-mesa += "${libdir}/libGLX_mesa.so.*"
FILES_libgl-mesa-dev += "${libdir}/libGLX_mesa.so"
PACKAGES =+ "${PN}-stubs-dev ${PN}-stubs"
FILES_${PN}-stubs = "${libdir}/mesa/lib*${SOLIBS}"
FILES_${PN}-stubs-dev = "${libdir}/mesa/lib*${SOLIBSDEV}"
ALLOW_EMPTY_${PN}-stubs = "1"
ALLOW_EMPTY_${PN}-stubs-dev = "1"
PRIVATE_LIBS_${PN}-stubs = "\
    libEGL.so.1 \
    libGLESv1_CM.so.1 \
    libGLESv2.so.2 \
    libGL.so.1 \
"
