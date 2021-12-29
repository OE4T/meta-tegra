L4T_DEB_COPYRIGHT_MD5 = "3d9212d4d5911fa3200298cd55ed6621"
DEPENDS = "tegra-libraries-core tegra-libraries-cuda tegra-libraries-multimedia-utils tegra-libraries-nvsci pango cairo glib-2.0 gstreamer1.0 gstreamer1.0-plugins-base virtual/egl"

require tegra-debian-libraries-common.inc

LICENSE += "& MIT & BSD-3-Clause"
LIC_FILES_CHKSUM += "\
    file://usr/share/doc/nvidia-tegra/LICENSE.libnveventlib;md5=42479ac5ddc96ba7997ecf0636e707d2 \
    file://usr/share/doc/nvidia-tegra/LICENSE.libnvtracebuf;md5=42479ac5ddc96ba7997ecf0636e707d2 \
    file://usr/share/doc/nvidia-tegra/LICENSE.libnvjpeg;md5=1b873f8976e4e3683c04133e3035be98 \
"

<<<<<<<< HEAD:recipes-bsp/tegra-binaries/tegra-libraries-multimedia_32.7.2.bb
MAINSUM = "615f144019af8f083e46ac7db005c087d953856795fc26fc528f323bc52763bb"
MAINSUM:tegra210 = "b6e211042ae57390cea2add1aad52903b31fbc950ac9b6613c4b30e5974092e6"
========
MAINSUM = "8df6cf44eecbe35690163b1bbb65cc4cf4160494a847ef01f1f2ba8702ebc392"
>>>>>>>> 390a51f3 (tegra-binaries: update for 34.1.0):recipes-bsp/tegra-binaries/tegra-libraries-multimedia_34.1.0.bb

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvbufsurftransform.so.1.0.0 \
    tegra/libnvdecode2eglimage.so \
    tegra/libnvdsbufferpool.so.1.0.0 \
    tegra/libnveventlib.so \
    tegra/libnvexif.so \
    tegra/libnvid_mapper.so.1.0.0 \
    tegra/libnvjpeg.so \
    tegra/libnvmedia.so \
    tegra/libnvmedia2d.so \
    tegra/libnvmedia_2d.so \
    tegra/libnvmedia_dla.so \
    tegra/libnvmedialdc.so \
    tegra/libnvmedia_tensor.so \
    tegra/libnvmm.so \
    tegra/libnvmm_contentpipe.so \
    tegra/libnvmm_parser.so \
    tegra/libnvmm_utils.so \
    tegra/libnvmmlite.so \
    tegra/libnvmmlite_image.so \
    tegra/libnvmmlite_utils.so \
    tegra/libnvmmlite_video.so \
    tegra/libnvofsdk.so \
    tegra/libnvosd.so \
    tegra/libnvparser.so \
    tegra/libnvtracebuf.so \
    tegra/libnvtvmr.so \
    tegra/libnvtvmr_2d.so \
    tegra/libnvvideo.so \
"

do_install() {
    install_libraries
    for libname in nvdsbufferpool nvbufsurface nvbufsurftransform nvid_mapper; do
	ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so.1
	ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so
    done
}

PACKAGES =+ "${PN}-osd"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
FILES:${PN}-osd = "${libdir}/libnvosd*"
RDEPENDS:${PN}-osd = "liberation-fonts"
