DESCRIPTION = "NVIDIA Tegra Multimedia API headers"

require tegra-mmapi-${PV}.inc

DEPENDS = "tegra-libraries virtual/egl"

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
    install -m 0644 ${S}/include/nvbuf_utils.h ${D}${includedir}/
    install -m 0644 ${S}/include/nvosd.h ${D}${includedir}/
}

PACKAGES = "${PN}-dev ${PN}"
ALLOW_EMPTY_${PN} = "1"
