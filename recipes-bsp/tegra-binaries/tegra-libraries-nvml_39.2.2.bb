L4T_DEB_COPYRIGHT_MD5 = "4108d6904a9ac6948333d8c6b1d756e5"
DEPENDS = "tegra-libraries-core tegra-libraries-eglcore"

require tegra-debian-libraries-common.inc

MAINSUM = "0e3fe7d86fd8d44792656b7c4bfc482083a7fa1f7bf27e8304a3ae3758f46c08"

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
