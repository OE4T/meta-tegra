L4T_DEB_COPYRIGHT_MD5 = "d5a415a7af91ce262eed42e9c9fba7d1"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

LICENSE += "& MIT"
LIC_FILES_CHKSUM += "file://usr/share/doc/nvidia-tegra/LICENSE.libnvscibuf;md5=0cd5a346aecd6451e0224bf024e84756"

MAINSUM = "d17a2814e513db6cb45d35d65d80f82a0d05c4d635fd151882176b58f66c57c6"

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
RRECOMMENDS:${PN} = "kernel-module-nvgpu"
