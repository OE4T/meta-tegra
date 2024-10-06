DESCRIPTION = "NVIDIA GStreamer applications"
SECTION = "multimedia"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://nvgst_sample_apps/nvgstcapture-1.0/nvgstcapture-1.0_README.txt;endline=21;md5=75e7c2d08d6618cb836cbef46b410515 \
                    file://nvgst_sample_apps/nvgstplayer-1.0/nvgstplayer-1.0_README.txt;endline=21;md5=694cc29d69c54345f88511643308aae5 \
"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/nvgstapps_src.tbz2"

require recipes-bsp/tegra-sources/tegra-sources-36.4.0.inc

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base virtual/egl libx11 libxext"

SRC_URI += "file://0002-Fix-stringop-truncation-warning.patch"

S = "${WORKDIR}/nvgstapps_src"
B = "${WORKDIR}/build"

inherit pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "x11 opengl"
PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'alsa', d)}"
PACKAGECONFIG[alsa] = "WITH_NVGSTPLAYER=yes,,alsa-lib"

CFLAGS += "-Wall -Werror -Wno-deprecated-declarations -DNVGST_TARGET_TEGRA"

do_compile() {
    ${PACKAGECONFIG_CONFARGS}
    ${CC} ${CFLAGS} -o ${B}/nvgstcapture-1.0 ${S}/nvgst_sample_apps/nvgstcapture-1.0/nvgstcapture.c \
        ${S}/nvgst_sample_apps/nvgstcapture-1.0/nvgst_x11_common.c ${LDFLAGS} \
        $(pkg-config --cflags --libs gstreamer-1.0 gstreamer-plugins-base-1.0 gstreamer-pbutils-1.0 x11 xext gstreamer-video-1.0) -ldl
    if [ "$WITH_NVGSTPLAYER" = "yes" ]; then
        ${CC} ${CFLAGS} -o ${B}/nvgstplayer-1.0 ${S}/nvgst_sample_apps/nvgstplayer-1.0/nvgstplayer.c \
            ${S}/nvgst_sample_apps/nvgstplayer-1.0/nvgst_asound_common.c ${S}/nvgst_sample_apps/nvgstplayer-1.0/nvgst_x11_common.c ${LDFLAGS} \
            $(pkg-config --cflags --libs gstreamer-1.0 gstreamer-plugins-base-1.0 gstreamer-pbutils-1.0 x11 xext gstreamer-video-1.0 alsa) -ldl
    fi
}

do_install() {
    ${PACKAGECONFIG_CONFARGS}
    install -d ${D}${bindir}
    install -m 0755 ${B}/nvgstcapture-1.0 ${D}${bindir}/
    if [ "$WITH_NVGSTPLAYER" = "yes" ]; then
        install -m 0755 ${B}/nvgstplayer-1.0 ${D}${bindir}/
    fi
}

PACKAGES =+ "nvgstcapture nvgstplayer"
FILES:nvgstplayer = "${bindir}/nvgstplayer-1.0"
FILES:nvgstcapture = "${bindir}/nvgstcapture-1.0"
ALLOW_EMPTY:nvgstplayer = "1"
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} = "nvgstcapture nvgstplayer"
RRECOMMENDS:nvgstcapture = "gstreamer1.0-plugins-nvarguscamerasrc gstreamer1.0-plugins-nvv4l2camerasrc gstreamer1.0-plugins-good-video4linux2 gstreamer1.0-plugins-tegra"
RRECOMMENDS:nvgstplayer = "gstreamer1.0-plugins-nveglgles gstreamer1.0-plugins-nvvideo4linux2 gstreamer1.0-plugins-nvvideosinks gstreamer1.0-plugins-nvjpeg gstreamer1.0-plugins-tegra"
