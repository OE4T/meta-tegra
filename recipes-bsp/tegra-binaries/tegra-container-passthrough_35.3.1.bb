L4T_DEB_COPYRIGHT_MD5 = "3d9212d4d5911fa3200298cd55ed6621"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "\
    ${@l4t_deb_pkgname(d, 'camera')};subdir=${BP};name=camera \
    ${@l4t_deb_pkgname(d, 'gstreamer')};subdir=${BP}/full;name=gstreamer \
    ${@l4t_deb_pkgname(d, 'wayland')};subdir=${BP}/full;name=wayland \
    ${@l4t_deb_pkgname(d, 'weston')};subdir=${BP}/full;name=weston \
    ${@l4t_deb_pkgname(d, 'libvulkan')};subdir=${BP}/full;name=libvulkan \
"
MAINSUM = "83dd039e6e790fd0aae4a1e68101ba1ac75b661038142e6ded826a35ff27376e"
SRC_URI[camera.sha256sum] = "e745e00cb01b629526fdeb8d8c2b24fb0c286e605229c61389a03566ae2005f3"
SRC_URI[gstreamer.sha256sum] = "a3c7dc8a2ea2bc2b6ebeb52d108e49944b70099ef7aa50e8144e9a4860f81f11"
SRC_URI[wayland.sha256sum] = "c2acb375787f64ffca6820e29190c0d6df2d2f19613f215bd8334b38bb214a17"
SRC_URI[weston.sha256sum] = "818a9b1af77ab3a732076280335d623a321cad60ee4650fb461f7a31839fda41"
SRC_URI[libvulkan.sha256sum] = "e99de55cebd7a1d83546822c2095e3471ea51d24f769d7d25ec4344ba3bf66ea"

PASSTHRU_ROOT = "${datadir}/nvidia-container-passthrough"

do_install() {
    install -d ${D}${PASSTHRU_ROOT}/usr/lib
    # 'full' subdirectory is where we dumped the pacakges that we just copy in full
    cp -R --preserve=mode,links,timestamps ${S}/full/usr/lib/aarch64-linux-gnu ${D}${PASSTHRU_ROOT}/usr/lib/
    # Just the V4L2 files for the multimedia package
    cp -R --preserve=mode,links,timestamps ${S}/usr/lib/aarch64-linux-gnu/libv4l ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/
    for f in libnvv4l2.so libnvv4lconvert.so libv4l2_nvvideocodec.so libv4l2_nvcuvidvideocodec.so libv4l2_nvargus.so; do
        install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/tegra/$f ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/tegra/
    done
    ln -sf libnvv4l2.so ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/tegra/libv4l2.so.0
    ln -sf libnvv4lconvert.so ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/tegra/libv4lconvert.so.0
    ln -sf tegra/libv4l2.so.0 ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/libv4l2.so.0.0.999999
    ln -sf libv4l2.so.0.0.999999 ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/libv4l2.so.0
    ln -sf tegra/libv4lconvert.so.0 ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/libv4lconvert.so.0.0.999999
    ln -sf libv4lconvert.so.0.0.999999 ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/libv4lconvert.so.0
    ln -sf libgstreamer-1.0.so.0.1603.99999 ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/libgstreamer-1.0.so.0
}

EXCLUDE_FROM_SHLIBS = "1"
SKIP_FILEDEPS = "1"
FILES:${PN} = "${PASSTHRU_ROOT}"
INSANE_SKIP:${PN} = "textrel"
