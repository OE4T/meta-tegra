PROVIDES_remove_tegra210 = "virtual/libgl virtual/libgles1 virtual/libgles2 virtual/libopenvg virtual/egl"

python () {
    overrides = d.getVar("OVERRIDES", True).split(":")
    if "tegra210" not in overrides:
        return

    extra_oeconf = d.getVar("EXTRA_OECONF", True)
    extra_oeconf = extra_oeconf.replace("--enable-glx-tls", "--enable-glx")
    d.setVar("EXTRA_OECONF", extra_oeconf)
}

do_install_append_tegra210() {
    rm -f ${D}${libdir}/libGL.*
    rm -f ${D}${libdir}/libGLES*
    rm -f ${D}${libdir}/libEGL.*
}

PACKAGE_ARCH_tegra210 = "${SOC_FAMILY_PKGARCH}"
