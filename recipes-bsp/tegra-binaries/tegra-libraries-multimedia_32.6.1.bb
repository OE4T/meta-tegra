DEPENDS = "tegra-libraries-core tegra-libraries-cuda tegra-libraries-multimedia-utils pango cairo glib-2.0 gstreamer1.0 gstreamer1.0-plugins-base virtual/egl"

require tegra-libraries-common.inc

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvavp.so \
    tegra/libnvbufsurface.so.1.0.0 \
    tegra/libnvbufsurftransform.so.1.0.0 \
    tegra/libnvdecode2eglimage.so \
    tegra/libnvdsbufferpool.so.1.0.0 \
    tegra/libnveventlib.so \
    tegra/libnvexif.so \
    tegra/libnvid_mapper.so.1.0.0 \
    tegra/libnvjpeg.so \
    tegra/libnvmedia.so \
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
    tegra/libnvtestresults.so \
    tegra/libnvtnr.so \
    tegra/libnvtracebuf.so \
    tegra/libnvtvmr.so \
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
FILES_${PN}-osd = "${libdir}/libnvosd*"
RDEPENDS_${PN}-osd = "liberation-fonts"
