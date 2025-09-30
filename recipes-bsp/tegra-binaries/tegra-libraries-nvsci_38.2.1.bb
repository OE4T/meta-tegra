L4T_DEB_COPYRIGHT_MD5 = "661953981b9ae6f275ad01c67b2e881e"
DEPENDS = "tegra-libraries-core tegra-libraries-adaruntime"

require tegra-debian-libraries-common.inc

LICENSE += "& MIT"
LIC_FILES_CHKSUM += "file://usr/share/doc/nvidia-l4t-nvsci/LICENSE.libnvscibuf;md5=0cd5a346aecd6451e0224bf024e84756"

MAINSUM = "fa8a9c90163726340c2d8b558abaa02e85406ab7d219e9807fad7247f42c9be9"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvscibuf.so.1 \
    nvidia/libnvscicommon.so.1 \
    nvidia/libnvscievent.so \
    nvidia/libnvscistream.so.1 \
    nvidia/libnvscisync.so.1 \
"
do_install() {
    install_libraries
    for libname in nvscibuf nvscicommon nvscistream nvscisync; do
	ln -sf lib$libname.so.1 ${D}${libdir}/lib$libname.so
    done
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RPROVIDES:${PN} += "libnvscibuf.so()(64bit)"
