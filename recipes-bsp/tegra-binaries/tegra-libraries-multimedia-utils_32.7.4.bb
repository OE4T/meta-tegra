L4T_DEB_COPYRIGHT_MD5 = "9b04019d6032ae79ef58a07a2f55c2b7"
DEPENDS = "tegra-libraries-core virtual/egl"

require tegra-debian-libraries-common.inc

MAINSUM = "ff227a2309781243b96e07eb5ad00b8d5c7cec11688025fdbfce20ab3a677900"
MAINSUM_tegra210 = "a2c4c0661d213226e4afde47aa2dbf328a86cb8bf8f66ee26387841735fc1d21"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvbuf_fdmap.so.1.0.0 \
    tegra/libnvbuf_utils.so.1.0.0 \
"

do_install() {
    install_libraries
    for libname in nvbuf_utils; do
	ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so.1
	ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so
    done
}
