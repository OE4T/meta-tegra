L4T_DEB_COPYRIGHT_MD5 = "91cf75b2c9b94ab2c5ee5b09aafb954e"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

MAINSUM = "930153c7be15bbc835aef2779a50f4114f3cf455924f6f54c5bdd3e93597f9a6"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvidia-ml.so.1 \
"
do_install() {
    install_libraries
    for libname in nvidia-ml; do
	ln -sf lib$libname.so.1 ${D}${libdir}/lib$libname.so
    done
    install -D -m 0755 ${S}/usr/sbin/nvidia-smi ${D}${sbindir}/nvidia-smi
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RRECOMMENDS:${PN} = "kernel-module-nvgpu"
