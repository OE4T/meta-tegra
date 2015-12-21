PACKAGECONFIG_append_jetson-tx1 = " xinerama"
do_install_append_jetson-tx1() {
    rm -f ${D}${libdir}/xorg/modules/extensions/libglx.so
}
