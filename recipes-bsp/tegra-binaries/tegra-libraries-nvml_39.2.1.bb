L4T_DEB_COPYRIGHT_MD5 = "4108d6904a9ac6948333d8c6b1d756e5"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

MAINSUM = "6c3e78ad11e84c3c677f2d9c60473ca52103ed378faab9ccf6a83f756b884b21"

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
