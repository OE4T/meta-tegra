L4T_DEB_COPYRIGHT_MD5 = "8c84e973feeab684f7575379648f700c"
DEPENDS = "tegra-libraries-core tegra-libraries-cuda tegra-libraries-multimedia-utils tegra-libraries-nvsci pango cairo glib-2.0 virtual/egl"

require tegra-debian-libraries-common.inc

LICENSE += "& MIT & BSD-3-Clause"
LIC_FILES_CHKSUM += "\
    file://usr/share/doc/nvidia-tegra/LICENSE.libnveventlib;md5=42479ac5ddc96ba7997ecf0636e707d2 \
    file://usr/share/doc/nvidia-tegra/LICENSE.libnvtracebuf;md5=42479ac5ddc96ba7997ecf0636e707d2 \
    file://usr/share/doc/nvidia-tegra/LICENSE.libnvjpeg;md5=1b873f8976e4e3683c04133e3035be98 \
"

MAINSUM = "2f2e11941ec2242de50898e06d1f16669bcb0259efe5756b71d873f89fd26ca9"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvbufsurftransform.so.1.0.0 \
    tegra/libnvdecode2eglimage.so \
    tegra/libnveventlib.so \
    tegra/libnvexif.so \
    tegra/libnvid_mapper.so.1.0.0 \
    tegra/libnvjpeg.so \
    tegra/libnvmedia.so \
    tegra/libnvmedia2d.so \
    tegra/libnvmedia_2d.so \
    tegra/libnvmedia_dla.so \
    tegra/libnvmedialdc.so \
    tegra/libnvmedia_eglstream.so \
    tegra/libnvmedia_ide_parser.so \
    tegra/libnvmedia_ide_sci.so \
    tegra/libnvmedia_iep_sci.so \
    tegra/libnvmedia_ijpd_sci.so \
    tegra/libnvmedia_ijpe_sci.so \
    tegra/libnvmedia_iofa_sci.so \
    tegra/libnvmedia_sci_overlay.so \
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
    tegra/libnvoggopus.so \
    tegra/libnvosd.so \
    tegra/libnvparser.so \
    tegra/libnvtracebuf.so \
    tegra/libnvtvmr.so \
    tegra/libnvtvmr_2d.so \
    tegra/libnvvideo.so \
    tegra/libnvvideoencode_ppe.so \
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
