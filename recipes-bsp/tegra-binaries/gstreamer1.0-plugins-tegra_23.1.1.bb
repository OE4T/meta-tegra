require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_HOST = "(arm.*)"

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nv_sample_apps/nvgstapps.tbz2
}

do_compile[noexec] = "1"

LIBROOT = "${B}/usr/lib/arm-linux-gnueabihf"

NVGSTPLAYER = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'nvgstplayer', '', d)}"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/usr/bin/nvgstcapture-1.0 ${D}${bindir}
    if [ -n "${NVGSTPLAYER}" ]; then
        install -m 0755 ${B}/usr/bin/nvgstplayer-1.0 ${D}${bindir}
    fi
    install -d ${D}${libdir}/gstreamer-1.0
    install -m 0644 ${LIBROOT}/libgstnvegl-1.0.so.0 ${D}${libdir}
    install -m 0644 ${LIBROOT}/libsample_process.so ${D}${libdir}
    for f in ${LIBROOT}/gstreamer-1.0/lib*; do
        install -m 0644 $f ${D}${libdir}/gstreamer-1.0/
    done
}

PACKAGES = "nvgstcapture ${NVGSTPLAYER} ${PN}"
FILES_nvgstcapture = "${bindir}/nvgstcapture-1.0"
RDEPENDS_nvcapture = "${PN}"
FILES_nvgstplayer = "${bindir}/nvgstplayer-1.0"
RDEPENDS_nvgstplayer = "${PN} virtual/libx11"
FILES_${PN} = "${libdir}"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP_${PN} = "dev-so ldflags build-deps"
INSANE_SKIP_${PN}-dev = "ldflags build-deps"
INSANE_SKIP_${MLPREFIX}nvgstcapture = "ldflags build-deps"
INSANE_SKIP_${MLPREFIX}nvgstplayer = "ldflags build-deps"
DEPENDS_${PN} = "gstreamer1.0 gstreamer1.0-plugins-good-jpeg"
