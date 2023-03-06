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
MAINSUM = "00aee7780face8a801d1bd612eaef2beaa01b72738acdbc3a83a3dd050830ced"
MAINSUM:tegra210 = "faf67ea3283106cbe825078e6d22c5d75ebae2a28d5c480071ffcd000ca8042e"

CAMERASUM = "cf77eeec2ecfd373fc20b1f77d3a1c3414fae5e9074a9bf3e35ace104a288646"
CAMERASUM:tegra210 = "036b6d6d400fb988274778f6d6028db5ce3a0a134c4fb6294672ddf8fc7f15a1"
SRC_URI[camera.sha256sum] = "${CAMERASUM}"

GSTREAMERSUM = "327efc561ff5d3f16a150f695bfc1e89f5bcd85bea1cc31f3fcc70cd71bbe885"
GSTREAMERSUM:tegra210 = "92b79e5fbac38552b7c4226598001d040f152c87362084f25f3716211bbab2fb"
SRC_URI[gstreamer.sha256sum] = "${GSTREAMERSUM}"

WAYLANDSUM = "9745a61201326560cd19106a472a6ffd8788e31d571146c2edf8bb95110351c4"
WAYLANDSUM:tegra210 = "b0f2acdb082ce891d72e1b4530e52e06afae075f6b2c461b7d11f91eed26fd8b"
SRC_URI[wayland.sha256sum] = "${WAYLANDSUM}"

WESTONSUM = "ef41d4134f4a5fe8fa6e5d662daef18e2de8deb8e21c284b0c28d84bc599e655"
WESTONSUM:tegra210 = "d6996a01d6f388dc9ce4847541448658d0eb129ada032920832014669e86224c"
SRC_URI[weston.sha256sum] = "${WESTONSUM}"

LIBVULKANSUM = "f7957148490060e2c8b914c833db883dd21d09c69533b8940ee9f0e8119b807f"
LIBVULKANSUM:tegra210 = "a37cfbf308a2179222d7a9268a242d058cdcea4a125dbe7a755864275d997237"
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