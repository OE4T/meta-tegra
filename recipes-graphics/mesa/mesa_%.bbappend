FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += "file://egl-gles2-nv-extensions.patch"

DEPENDS += "tegra-mmapi-glheaders"

PACKAGECONFIG[glvnd] = "--enable-libglvnd,--disable-libglvnd,libglvnd"

PACKAGECONFIG_append_tegra = " glvnd"
DEPENDS_append_tegra = " tegra-libraries"
EXTRA_OECONF_append_tegra = " --without-dri-drivers --disable-dri3"

python () {
    overrides = d.getVar("OVERRIDES").split(":")
    if "tegra" not in overrides:
        return

    x11flag = d.getVarFlag("PACKAGECONFIG", "x11", False)
    d.setVarFlag("PACKAGECONFIG", "x11", x11flag.replace("--enable-glx-tls", "--enable-glx"))
}

do_install_append() {
    if ${@bb.utils.contains("PACKAGECONFIG", "glvnd", "true", "false", d)}; then
        rm -f ${D}${libdir}/libGLESv1_CM.so* ${D}${libdir}/libGLESv2.so*
	sed -i -e's,lGLX_mesa,lGL,' ${D}${libdir}/pkgconfig/gl.pc
    fi
}

PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"
FILES_libegl-mesa += "${libdir}/libEGL_mesa.so.* ${datadir}/glvnd"
FILES_libegl-mesa-dev += "${libdir}/libEGL_mesa.so"
FILES_libgl-mesa += "${libdir}/libGLX_mesa.so.*"
FILES_libgl-mesa-dev += "${libdir}/libGLX_mesa.so"
