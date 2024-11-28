L4T_DEB_COPYRIGHT_MD5 = "da66dd592b6aab6a884628599ea927fe"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "\
    ${@l4t_deb_pkgname(d, 'camera')};subdir=${BP};name=camera \
    ${@l4t_deb_pkgname(d, 'gstreamer')};subdir=${BP}/full;name=gstreamer \
    ${@l4t_deb_pkgname(d, 'wayland')};subdir=${BP}/full;name=wayland \
    ${@l4t_deb_pkgname(d, 'weston')};subdir=${BP}/full;name=weston \
    ${@l4t_deb_pkgname(d, 'libvulkan')};subdir=${BP}/full;name=libvulkan \
"
MAINSUM = "6589ce63514866c94db9fc76c0baa395397aa51cef6b191435a4520a97704cc2"
MAINSUM:tegra210 = "e85b22ac1988e7e8926258efd7a54b16fa4fcdbb15810b73dc817570524f0d7b"

CAMERASUM = "0043810a8994ba8171677d45e709443d02a487191bd61c363bf8396c8f9224d2"
CAMERASUM:tegra210 = "cba6d67cbfc95765459489290e948b6d86fde65d045b8b58eca9ef01906faf3b"
SRC_URI[camera.sha256sum] = "${CAMERASUM}"

GSTREAMERSUM = "f396b2c7b4a73a64ac5d8e3ca1ce7f501bc9c6bcd3a9052570eb9bae46b16bfc"
GSTREAMERSUM:tegra210 = "9608bace9efa0a3cb29c19500a917ff3f0f0fce5451dee3fc9f6bc01d47b41d2"
SRC_URI[gstreamer.sha256sum] = "${GSTREAMERSUM}"

WAYLANDSUM = "ecd94c8a746985144365781630992b88238e60a2bd964a7da28237ba009b6ab5"
WAYLANDSUM:tegra210 = "8c61cd9cf001b7948104d8645e29b3c79b83c65d59203ea1f35e911a8721c42a"
SRC_URI[wayland.sha256sum] = "${WAYLANDSUM}"

WESTONSUM = "e6fea7ac58260f6da0325988db35cba169dfd8eadccade1f6adb5d570f49d207"
WESTONSUM:tegra210 = "023ec466b0651adc63b14d912dc4ca2ef3a1b153ccfaeb5432dcfe49fca8846a"
SRC_URI[weston.sha256sum] = "${WESTONSUM}"

LIBVULKANSUM = "a72e9d6868f045e3a706cb825ab3cc3f83a2f446f8ef4374eac92a50d7000df7"
LIBVULKANSUM:tegra210 = "b05a7e28acb9d6023efbabbefaad6b514bb2eea4310923fab84eba71c6e1b925"
SRC_URI[libvulkan.sha256sum] = "${LIBVULKANSUM}"

PASSTHRU_ROOT = "${datadir}/nvidia-container-passthrough"

do_install() {
    install -d ${D}${PASSTHRU_ROOT}/usr/lib
    # 'full' subdirectory is where we dumped the pacakges that we just copy in full
    cp -R --preserve=mode,links,timestamps ${S}/full/usr/lib/aarch64-linux-gnu ${D}${PASSTHRU_ROOT}/usr/lib/
    # Just the V4L2 files for the multimedia package
    cp -R --preserve=mode,links,timestamps ${S}/usr/lib/aarch64-linux-gnu/libv4l ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/
    for f in libnvv4l2.so libnvv4lconvert.so libv4l2_nvvideocodec.so libv4l2_nvcuvidvideocodec.so libv4l2_nvargus.so; do
    # Library libv4l2_nvcuvidvideocodec.so not present in tegra210
        [ -f "${S}/usr/lib/aarch64-linux-gnu/tegra/$f" ] &&  install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/tegra/$f ${D}${PASSTHRU_ROOT}/usr/lib/aarch64-linux-gnu/tegra/
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

CONTAINER_CSV_FILES = ""
CONTAINER_CSV_PKGNAME = ""
