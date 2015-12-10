require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_HOST = "(arm.*)"

do_configure() {
    tar -C ${B} -x -f ${S}/nv_sample_apps/nvgstapps.tbz2
}

do_compile[noexec] = "1"

LIBROOT = "${B}/usr/lib/arm-linux-gnueabihf"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/usr/bin/nvgstcapture-1.0 ${D}${bindir}
    install -m 0755 ${B}/usr/bin/nvgstplayer-1.0 ${D}${bindir}
    install -d ${D}${libdir}/gstreamer-1.0
    install -m 0644 ${LIBROOT}/libgstnvegl-1.0.so.0 ${D}${libdir}
    install -m 0644 ${LIBROOT}/libsample_process.so ${D}${libdir}
    for f in ${LIBROOT}/gstreamer-1.0/lib*; do
        install -m 0644 $f ${D}${libdir}/gstreamer-1.0/
    done
}


PACKAGES = "nvgstcapture nvgstplayer ${PN}"
FILES_nvgstcapture = "${bindir}/nvgstcapture-1.0"
RDEPENDS_nvcapture = "${PN}"
FILES_nvgstplayer = "${bindir}/nvgstplayer-1.0"
RDEPENDS_nvgstplayer = "${PN}"

INSANE_SKIP_${PN} = "dev-so"
RDEPENDS_${PN} = "gstreamer1.0 gstreamer1.0-plugins-good-jpeg"
