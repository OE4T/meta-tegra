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
MAINSUM = "505ca0ca10fd91eae99f0c441beb73b543e796d9c0d013122684a1fa2db7a29e"
SRC_URI[camera.sha256sum] = "b6f5c02926388fc3fc4ff3b3ad4c123a4f5eaec8313b047b4fc91a27e5205274"
SRC_URI[gstreamer.sha256sum] = "76567c73f13ddf0cc837caa5c143bcf447b5667f28e18a7108df10868476f4b0"
SRC_URI[wayland.sha256sum] = "22b16e5dcfba3234bad9fa48d739a50f2a06533a0e7e4f36c980243bfa9ff4c7"
SRC_URI[weston.sha256sum] = "cd731ee675f9b02ec84f15a79d11dd6b064180618c61b013931aaeaaf4528b13"
SRC_URI[libvulkan.sha256sum] = "0c9f84172e59fa9af38e3e7ca743cf4954338f2087d1d8c50b4a494a820005f8"

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
