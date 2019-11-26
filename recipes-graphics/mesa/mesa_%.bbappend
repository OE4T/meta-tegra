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
