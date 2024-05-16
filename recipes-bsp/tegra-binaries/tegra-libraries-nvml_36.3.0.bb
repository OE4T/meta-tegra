L4T_DEB_COPYRIGHT_MD5 = "91cf75b2c9b94ab2c5ee5b09aafb954e"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

MAINSUM = "11a4cd5c640566f88247bee3baba54c66f7fd2a80539e8b6ae86d877bffa05e7"

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
