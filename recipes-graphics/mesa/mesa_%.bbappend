PACKAGECONFIG[glvnd] = "-Dglvnd=true,-Dglvnd=false,libglvnd"

GLVNDCFG = ""
GLVNDCFG_tegra = " glvnd"
PACKAGECONFIG_append_class-target = "${GLVNDCFG}"
PROVIDES_tegra = "virtual/mesa virtual/libgbm"

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
    fi
}

PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"
FILES_libegl-mesa += "${libdir}/libEGL_mesa.so.* ${datadir}/glvnd"
FILES_libegl-mesa-dev += "${libdir}/libEGL_mesa.so"
FILES_libgl-mesa += "${libdir}/libGLX_mesa.so.*"
FILES_libgl-mesa-dev += "${libdir}/libGLX_mesa.so"

RPROVIDES_${PN}_tegra += "libgbm"
