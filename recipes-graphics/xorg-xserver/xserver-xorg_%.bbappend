PACKAGECONFIG_append_tegra210 = " xinerama"
do_install_append_tegra210() {
    rm -f ${D}${libdir}/xorg/modules/extensions/libglx.so
}
