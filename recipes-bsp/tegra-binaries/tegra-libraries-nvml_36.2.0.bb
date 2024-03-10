L4T_DEB_COPYRIGHT_MD5 = "1c811e7c402eb9dd6e54dd9e4c82afba"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

MAINSUM = "e6a537f1a7f4d51ca05f8c26df803ace70d12f6f8d5d7a706d7eb6dfcc2df4a2"

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
