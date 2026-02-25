DESCRIPTION = "NVIDIA Tegra Multimedia API headers subset for Argus, nvosd, and nvbuf APIs"

require tegra-mmapi-${PV}.inc

DEPENDS = "tegra-libraries-multimedia tegra-libraries-multimedia-utils tegra-libraries-camera virtual/egl"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}${includedir}
    cp -R --preserve=mode,timestamps ${S}/argus/include/Argus ${D}${includedir}
    cp -R --preserve=mode,timestamps ${S}/argus/include/EGLStream ${D}${includedir}
    cp -R --preserve=mode,timestamps ${S}/include/libjpeg-8b ${D}${includedir}
    install -m 0644 ${S}/include/gstnvdsseimeta.h ${D}${includedir}/
    install -m 0644 ${S}/include/nvbufsurface.h ${D}${includedir}/
    install -m 0644 ${S}/include/nvbufsurftransform.h ${D}${includedir}/
    install -m 0644 ${S}/include/v4l2_nv_extensions.h ${D}${includedir}/
    install -m 0644 ${S}/include/nvosd.h ${D}${includedir}/
    # Needed by gstreamer1.0-plugins-nvarguscamerasrc
    install -d ${D}${includedir}/Argus/utils
    install -m 0644 ${S}/argus/samples/utils/Ordered.h ${D}${includedir}/Argus/utils/
    install -m 0644 ${S}/argus/samples/utils/Error.h ${D}${includedir}/Argus/utils/
    # Needed by gstreamer1.0-plugins-gst-nvsiplcamerasrc
    install -m 0644 ${S}/include/nvbufsurface_nvscibuf.h ${D}${includedir}/
}

PACKAGES = "${PN}-dev ${PN}"
ALLOW_EMPTY:${PN} = "1"
