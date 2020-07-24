EXTRA_OEMESON_append_tegra = " -Dglvnd=true"
DEPENDS_append_tegra = " libglvnd"
PROVIDES_tegra = "virtual/mesa virtual/libgbm"

# Workaround for the do_install_append() present in the OE-Core recipe
do_install_prepend_tegra() {
    install -d ${D}${includedir}/EGL
    touch ${D}${includedir}/EGL/eglplatform.h
}

do_install_append_tegra() {
    rm -rf ${D}${includedir}/EGL
}

PACKAGE_ARCH_tegra = "${TEGRA_PKGARCH}"
FILES_libegl-mesa_append_tegra = " ${libdir}/libEGL_mesa.so.* ${datadir}/glvnd"
FILES_libegl-mesa-dev_append_tegra = " ${libdir}/libEGL_mesa.so"
FILES_libgl-mesa_append_tegra = " ${libdir}/libGLX_mesa.so.*"
FILES_libgl-mesa-dev_append_tegra = " ${libdir}/libGLX_mesa.so"

python __anonymous() {
    if "tegra" not in d.getVar('OVERRIDES').split(':'):
        return
    pkgconfig = (d.getVar('PACKAGECONFIG') or '').split();
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
