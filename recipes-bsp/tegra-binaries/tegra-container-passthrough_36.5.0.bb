L4T_DEB_COPYRIGHT_MD5 = "c5a9810a8ac2bdcdce4e85013d7044d4"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "\
    ${@l4t_deb_pkgname(d, 'camera')};subdir=${BP};name=camera \
    ${@l4t_deb_pkgname(d, 'wayland')};subdir=${BP}/full;name=wayland \
    ${@l4t_deb_pkgname(d, 'weston')};subdir=${BP}/full;name=weston \
"
SRC_COMMON_DEBS += "\
    ${@l4t_deb_pkgname(d, 'gstreamer')};subdir=${BP}/full;name=gstreamer \
"
MAINSUM = "03069358ddda34be5884740d49c0dd8a5154571a0086074376fe1809be4d9d91"
SRC_URI[camera.sha256sum] = "7b54e62ce54b09288da4f2d92faf8ad5c3e399fb1e45c31f5e2d654368008f0d"
SRC_URI[gstreamer.sha256sum] = "188189e005aeb9445aa33134573056cf82b203a989c33e75fa6798fb8265e3e8"
SRC_URI[wayland.sha256sum] = "9daf43bf3bc84b66a7253a1a35785b8c1498d66bad0ddf69c0b2d8ac3f2c3240"
SRC_URI[weston.sha256sum] = "c386592dc47d7b34c1017b2a8210054da24ad7cf19021a63fc25f5c13a6267eb"

PASSTHRU_ROOT = "${datadir}/nvidia-container-passthrough"

do_install() {
    install -d ${D}${PASSTHRU_ROOT}/usr/lib
    # 'full' subdirectory is where we dumped the pacakges that we just copy in full
    cp -R --preserve=mode,links,timestamps ${S}/full/usr/lib/aarch64-linux-gnu ${D}${PASSTHRU_ROOT}/usr/lib/
    # Just the V4L2 files for the multimedia package
    cp -R --preserve=mode,links,timestamps ${S}/usr/lib/aarch64-linux-gnu/libv4l ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/
    for f in libnvv4l2.so libnvv4lconvert.so libv4l2_nvvideocodec.so libv4l2_nvcuvidvideocodec.so libv4l2_nvargus.so; do
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
