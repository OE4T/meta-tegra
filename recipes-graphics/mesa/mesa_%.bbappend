DEPENDS_append_tegra124 = " tegra-libraries"
DEPENDS_append_tegra186 = " tegra-libraries"
DEPENDS_append_tegra210 = " tegra-libraries"

python () {
    overrides = d.getVar("OVERRIDES").split(":")
    if "tegra210" not in overrides and "tegra124" not in overrides and "tegra186" not in overrides:
        return

    x11flag = d.getVarFlag("PACKAGECONFIG", "x11", False)
    d.setVarFlag("PACKAGECONFIG", "x11", x11flag.replace("--enable-glx-tls", "--enable-glx"))
}

do_install_append_tegra210() {
    rm -f ${D}${libdir}/libGL.*
    rm -f ${D}${libdir}/libGLES*
    rm -f ${D}${libdir}/libEGL.*
}

do_install_append_tegra124() {
    rm -f ${D}${libdir}/libGL.*
    rm -f ${D}${libdir}/libGLES*
    rm -f ${D}${libdir}/libEGL.*
}

do_install_append_tegra186() {
    rm -f ${D}${libdir}/libGL.*
    rm -f ${D}${libdir}/libGLES*
    rm -f ${D}${libdir}/libEGL.*
}

PACKAGE_ARCH_tegra210 = "${SOC_FAMILY_PKGARCH}"
PACKAGE_ARCH_tegra124 = "${SOC_FAMILY_PKGARCH}"
PACKAGE_ARCH_tegra186 = "${SOC_FAMILY_PKGARCH}"
