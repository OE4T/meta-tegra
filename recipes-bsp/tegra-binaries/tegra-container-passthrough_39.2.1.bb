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
MAINSUM = "5f070f28008ff59a9f948dd8b0b7f6cc864285a595864043eb682efce913f7ca"
MULTIMEDIASUM = "PLEASE-UPDATE-THIS-RECIPE-WITH-MULTIMEDIA-DEB-PKG-INFO"
MULTIMEDIASUM:tegra264 = "6b886530befb3aacb6c931b0ec97c2b626fa5b0fc23dd43d4d16b87011fb68ef"
MULTIMEDIASUM:tegra234 = "b55df254f1a5e185170b3d7c1a92fe6e3d812e30d849c7141e11647a7dbd55d4"
SRC_URI[camera.sha256sum] = "b3a20033dc8131423f7e60b0005c58a03569d23bfa06aa3c4d7065ff208d8662"
SRC_URI[gstreamer.sha256sum] = "5101937ba4df642e2a054b5bc87ff6155703fee21ad1c85c134d6cf2b02b6f33"
SRC_URI[wayland.sha256sum] = "980cb672d5aa6a0fe1b817ba23bc1646344d8233f6cfb6cdb1923c0c3fcd8b20"
SRC_URI[weston.sha256sum] = "e755b030e728aa2253534c33873bd94226d60eec4bdba3deb6185228ce52c8ff"
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
