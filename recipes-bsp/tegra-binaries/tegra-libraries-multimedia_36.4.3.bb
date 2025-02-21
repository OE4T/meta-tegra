L4T_DEB_COPYRIGHT_MD5 = "770b0fc2a5cffa1d2b7eda7393e6b012"
DEPENDS = "tegra-libraries-core tegra-libraries-cuda tegra-libraries-multimedia-utils tegra-libraries-nvsci pango cairo glib-2.0 virtual/egl"

require tegra-debian-libraries-common.inc

LICENSE += "& MIT & BSD-3-Clause"
LIC_FILES_CHKSUM += "\
    file://usr/share/doc/nvidia-tegra/LICENSE.libnveventlib;md5=42479ac5ddc96ba7997ecf0636e707d2 \
    file://usr/share/doc/nvidia-tegra/LICENSE.libnvtracebuf;md5=42479ac5ddc96ba7997ecf0636e707d2 \
    file://usr/share/doc/nvidia-tegra/LICENSE.libnvjpeg;md5=1b873f8976e4e3683c04133e3035be98 \
"

MAINSUM = "a3067000eb63ebe954684367464e7f1de213abb0c78ec3400a59ceb9acdf69bb"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvbufsurftransform.so.1.0.0 \
    nvidia/libnvdecode2eglimage.so \
    nvidia/libnveventlib.so \
    nvidia/libnvexif.so \
    nvidia/libnvid_mapper.so.1.0.0 \
    nvidia/libnvjpeg.so \
    nvidia/libnvmedia.so \
    nvidia/libnvmedia2d.so \
    nvidia/libnvmedia_2d.so \
    nvidia/libnvmedia_dla.so \
    nvidia/libnvmedialdc.so \
    nvidia/libnvmedia_eglstream.so \
    nvidia/libnvmedia_ide_parser.so \
    nvidia/libnvmedia_ide_sci.so \
    nvidia/libnvmedia_iep_sci.so \
    nvidia/libnvmedia_ijpd_sci.so \
    nvidia/libnvmedia_ijpe_sci.so \
    nvidia/libnvmedia_iofa_sci.so \
    nvidia/libnvmedia_tensor.so \
    nvidia/libnvmm.so \
    nvidia/libnvmm_contentpipe.so \
    nvidia/libnvmm_parser.so \
    nvidia/libnvmm_utils.so \
    nvidia/libnvmmlite.so \
    nvidia/libnvmmlite_image.so \
    nvidia/libnvmmlite_utils.so \
    nvidia/libnvmmlite_video.so \
    nvidia/libnvofsdk.so \
    nvidia/libnvoggopus.so \
    nvidia/libnvosd.so \
    nvidia/libnvparser.so \
    nvidia/libnvtracebuf.so \
    nvidia/libnvtvmr.so \
    nvidia/libnvtvmr_2d.so \
    nvidia/libnvvideo.so \
    nvidia/libnvvideoencode_ppe.so \
"

do_install() {
    install_libraries
    for libname in nvbufsurftransform nvid_mapper; do
	ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so.1
	ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so
    done
}

PACKAGES =+ "${PN}-osd"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
FILES:${PN}-osd = "${libdir}/libnvosd*"
RDEPENDS:${PN}-osd = "liberation-fonts"
