DESCRIPTION = "Dummy recipe for Tegra gstreamer plugins metapackage"
LICENSE = "MIT"

PLUGINS = "\
    gstreamer1.0-omx-tegra \
    gstreamer1.0-plugins-nvarguscamerasrc \
    gstreamer1.0-plugins-nvdrmvideosink \
    gstreamer1.0-plugins-nveglgles \
    gstreamer1.0-plugins-nvjpeg \
    gstreamer1.0-plugins-nvtee \
    gstreamer1.0-plugins-nvv4l2camerasrc \
    gstreamer1.0-plugins-nvvidconv \
    gstreamer1.0-plugins-nvvideo4linux2 \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'gstreamer1.0-plugins-nvvideosinks', '', d)} \
    gstreamer1.0-plugins-tegra-binaryonly \
"
DEPENDS = "${PLUGINS}"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

COMPATIBLE_MACHINE = "(tegra)"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"

PACKAGES = "${PN} ${PN}-dev"
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} = "${PLUGINS}"
