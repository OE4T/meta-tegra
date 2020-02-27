PACKAGECONFIG[glvnd] = "--enable-libglvnd,--disable-libglvnd,libglvnd"

GLVNDCFG = ""
GLVNDCFG_tegra = " glvnd"
PACKAGECONFIG_append_class-target = "${GLVNDCFG}"
PROVIDES_tegra = "virtual/mesa virtual/libgbm"
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
	# libglvnd provides these headers
	for d in EGL GLES GLES2 GLES3 KHR; do
	    rm -rf ${D}${includedir}/$d
	done
	find ${D}${includedir}/GL -ignore_readdir_race -mindepth 1 -maxdepth 1 -name '*.h' -delete
        for pkgf in gl egl; do
	    if [ -e ${STAGING_LIBDIR}/pkgconfig/${pkgf}.pc ]; then
	       rm -f ${D}${libdir}/pkgconfig/${pkgf}.pc
	    fi
	done
	# libglvnd provides these files
	rm -rf ${D}${libdir}/libGLES*
	rm -rf ${D}${libdir}/pkgconfig/gles*.pc
    fi
}

PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"
FILES_libegl-mesa += "${libdir}/libEGL_mesa.so.* ${datadir}/glvnd"
FILES_libegl-mesa-dev += "${libdir}/libEGL_mesa.so"
FILES_libgl-mesa += "${libdir}/libGLX_mesa.so.*"
FILES_libgl-mesa-dev += "${libdir}/libGLX_mesa.so"

python __anonymous() {
    pkgconfig = (d.getVar('PACKAGECONFIG') or "").split()
    if "glvnd" not in pkgconfig:
        return
    for p in (("egl", "libegl", "libegl1"),
              ("dri", "libgl", "libgl1"),
              ("gles", "libgles1", "libglesv1-cm1"),
              ("gles", "libgles2", "libglesv2-2"),
              ("gles", "libgles3",)):
        if not p[0] in pkgconfig:
            continue
        fullp = p[1] + "-mesa"
        d.delVar("RREPLACES_" + fullp)
        d.delVar("RPROVIDES_" + fullp)
        d.delVar("RCONFLICTS_" + fullp)

        # For -dev, the first element is both the Debian and original name
        fullp += "-dev"
        d.delVar("RREPLACES_" + fullp)
        d.delVar("RPROVIDES_" + fullp)
        d.delVar("RCONFLICTS_" + fullp)
}
