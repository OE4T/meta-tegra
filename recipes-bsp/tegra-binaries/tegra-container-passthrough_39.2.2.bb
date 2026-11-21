L4T_DEB_COPYRIGHT_MD5 = "c5a9810a8ac2bdcdce4e85013d7044d4"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "\
    ${@l4t_deb_pkgname(d, 'camera')};subdir=${BP};name=camera \
    ${@l4t_deb_pkgname(d, 'wayland')};subdir=${BP}/full;name=wayland \
    ${@l4t_deb_pkgname(d, 'weston')};subdir=${BP}/full;name=weston \
"

CODEC_PLUGIN = "PLEASE-UPDATE-THIS-RECIPE-TO-SPECIFY-A-CODEC-PLUGIN"
SRC_SOC_DEBS:append:tegra264 = " ${@l4t_deb_pkgname(d, 'multimedia-openrm')};subdir=${BP};name=mm"
CODEC_PLUGIN:tegra264 = "libv4l2_nvcuvidvideocodec.so"
SRC_SOC_DEBS:append:tegra234 = " ${@l4t_deb_pkgname(d, 'multimedia-nvgpu')};subdir=${BP};name=mm"
CODEC_PLUGIN:tegra234 = "libv4l2_nvvideocodec.so"

SRC_COMMON_DEBS += "\
    ${@l4t_deb_pkgname(d, 'gstreamer')};subdir=${BP}/full;name=gstreamer \
"
MAINSUM = "d43aff2a973f1f3936c8f7b41dc18b1a483f87e27c96adbd2cc05754ea8aca49"
MULTIMEDIASUM = "PLEASE-UPDATE-THIS-RECIPE-WITH-MULTIMEDIA-DEB-PKG-INFO"
MULTIMEDIASUM:tegra264 = "efde0f36e8a0dee6bd8370f0ffa96972e2afccd507655b233eb1a1a30a78752f"
MULTIMEDIASUM:tegra234 = "3fb838952d92f955bb655eaa4985769fe9ed728a7e0952d520524b862c48c8ae"
SRC_URI[camera.sha256sum] = "4ee5fc8ab661f4140153a7753c0d28d617f59dd23553f8a84330ec6b0181e618"
SRC_URI[gstreamer.sha256sum] = "a42d90461511832152f8e170afb410998441d4f22116ec7af935890d8de645b6"
SRC_URI[wayland.sha256sum] = "dbff371d14cc590520055e78d3052ed373d14a34b5771d7a6b3ae68350e72359"
SRC_URI[weston.sha256sum] = "18885301ca928eb0d57272939950375433480929c64f5a108eecfec46ba1bd21"
SRC_URI[mm.sha256sum] = "${MULTIMEDIASUM}"

PASSTHRU_ROOT = "${datadir}/nvidia-container-passthrough"

do_install() {
    install -d ${D}${PASSTHRU_ROOT}/usr/lib
    # 'full' subdirectory is where we dumped the pacakges that we just copy in full
    cp -R --preserve=mode,links,timestamps ${S}/full/usr/lib/aarch64-linux-gnu ${D}${PASSTHRU_ROOT}/usr/lib/
    # Just the V4L2 files for the multimedia package
    cp -R --preserve=mode,links,timestamps ${S}/usr/lib/aarch64-linux-gnu/libv4l ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/
    for f in libnvv4l2.so libnvv4lconvert.so libv4l2_nvargus.so ${CODEC_PLUGIN}; do
        install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/nvidia/$f ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/nvidia/
    done
    ln -sf libnvv4l2.so ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/nvidia/libv4l2.so.0
    ln -sf libnvv4lconvert.so ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/nvidia/libv4lconvert.so.0
    ln -sf nvidia/libv4l2.so.0 ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/libv4l2.so.0.0.999999
    ln -sf libv4l2.so.0.0.999999 ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/libv4l2.so.0
    ln -sf nvidia/libv4lconvert.so.0 ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/libv4lconvert.so.0.0.999999
    ln -sf libv4lconvert.so.0.0.999999 ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/libv4lconvert.so.0
}

EXCLUDE_FROM_SHLIBS = "1"
SKIP_FILEDEPS = "1"
FILES:${PN} = "${PASSTHRU_ROOT}"
INSANE_SKIP:${PN} = "textrel"
EXTRA_RDEPS = ""
EXTRA_RDEPS:tegra264 = "tegra-libraries-video-codec"
RDEPENDS:${PN} = "${EXTRA_RDEPS}"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
