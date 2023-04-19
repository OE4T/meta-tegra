EXTRA_OEMESON:append:tegra = " -Dglvnd=true"
DEPENDS:append:tegra = " libglvnd"
PROVIDES:tegra = "virtual/mesa virtual/libgbm"

RDEPENDS:libgbm:append:tegra = " tegra-udrm-gbm"

# Workaround for the do_install:append() present in the OE-Core recipe
do_install:prepend:tegra() {
    install -d ${D}${includedir}/EGL
    touch ${D}${includedir}/EGL/eglplatform.h
}

# Remove the dummy file we installed above
do_install:append:tegra() {
    rm -f ${D}${includedir}/EGL/eglplatform.h
}

PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
FILES:libegl-mesa:append:tegra = " ${libdir}/libEGL_mesa.so.* ${datadir}/glvnd"
FILES:libegl-mesa-dev:append:tegra = " ${libdir}/libEGL_mesa.so"
FILES:libgl-mesa:append:tegra = " ${libdir}/libGLX_mesa.so.*"
FILES:libgl-mesa-dev:append:tegra = " ${libdir}/libGLX_mesa.so"

python __anonymous() {
    if "tegra" not in d.getVar('OVERRIDES').split(':'):
        return
    pkgconfig = (d.getVar('PACKAGECONFIG') or '').split();
    for p in (("egl", "libegl", "libegl1"),
              ("opengl", "libgl", "libgl1"),
              ("gles", "libgles1", "libglesv1-cm1"),
              ("gles", "libgles2", "libglesv2-2"),
              ("gles", "libgles3",)):
        if not p[0] in pkgconfig:
            continue
        fullp = p[1] + "-mesa"
        d.delVar("RREPLACES:" + fullp)
        d.delVar("RPROVIDES:" + fullp)
        d.delVar("RCONFLICTS:" + fullp)

        # For -dev, the first element is both the Debian and original name
        fullp += "-dev"
        d.delVar("RREPLACES:" + fullp)
        d.delVar("RPROVIDES:" + fullp)
        d.delVar("RCONFLICTS:" + fullp)
}
