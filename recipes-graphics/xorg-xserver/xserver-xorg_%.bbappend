PACKAGECONFIG_append_tegra210 = " xinerama"
do_install_append_tegra210() {
    rm -f ${D}${libdir}/xorg/modules/extensions/libglx.so
}

PACKAGE_ARCH_tegra210 = "${SOC_FAMILY_PKGARCH}"
