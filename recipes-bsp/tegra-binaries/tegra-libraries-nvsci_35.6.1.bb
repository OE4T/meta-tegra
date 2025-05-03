L4T_DEB_COPYRIGHT_MD5 = "d5a415a7af91ce262eed42e9c9fba7d1"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

LICENSE += "& MIT"
LIC_FILES_CHKSUM += "file://usr/share/doc/nvidia-tegra/LICENSE.libnvscibuf;md5=0cd5a346aecd6451e0224bf024e84756"

MAINSUM = "d555279e929165355ed795ed696763d8e111b7e53813e425e741a6324bd2da0b"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvscibuf.so.1 \
    tegra/libnvscicommon.so.1 \
    tegra/libnvscievent.so \
    tegra/libnvscistream.so.1 \
    tegra/libnvscisync.so.1 \
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
