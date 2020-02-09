PACKAGECONFIG[glvnd] = "-Dglvnd=true,-Dglvnd=false,libglvnd"

GLVNDCFG = ""
GLVNDCFG_tegra = " glvnd"
PACKAGECONFIG_append_class-target = "${GLVNDCFG}"
PROVIDES_tegra = "virtual/mesa virtual/libgbm"

# Workaround for the do_install_append() present in the OE-Core recipe
do_install_prepend() {
    if ${@bb.utils.contains("PACKAGECONFIG", "glvnd", "true", "false", d)}; then
        install -d ${D}${includedir}/EGL
	touch ${D}${includedir}/EGL/eglplatform.h
    fi
}

do_install_append() {
    if ${@bb.utils.contains("PACKAGECONFIG", "glvnd", "true", "false", d)}; then
        rm -rf ${D}${includedir}/EGL
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
