DESCRIPTION = "NVIDIA Tegra Multimedia API headers"
HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/011/linux-x64/Tegra_Multimedia_API_R${PV}_aarch64.tbz2"
SRC_URI[md5sum] = "1779e5ec38b2d35570aa5193c6738f10"
SRC_URI[sha256sum] = "f4fe088b692306dfce3b0f0058c1b8c7e52fde3e96d1a1ecd85a8af07f264346"

COMPATIBLE_MACHINE = "(jetsontx2)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

DEPENDS = "tegra-libraries"

LIC_FILES_CHKSUM = "file://LICENSE;md5=60ad17cc726658e8cf73578bea47b85f \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra_multimedia_api"
B = "${S}"

do_configure() {
    rm -rf ${S}/include/EGL ${S}/include/GL* ${S}/include/KHR* ${S}/include/libv4l2.h
}

do_compile() {
    sed -i -e's,jpeglib\.h,libjpeg-8b-tegra/jpeglib.h,' ${S}/include/NvJpeg*.h
}

do_install() {
    install -d ${D}${includedir}
    cp -R --preserve=mode,timestamps ${S}/argus/include/Argus ${D}${includedir}
    cp -R --preserve=mode,timestamps ${S}/argus/include/EGLStream ${D}${includedir}
    install -m 0644 ${S}/include/*.h ${D}${includedir}/
    install -d ${D}${includedir}/libjpeg-8b-tegra
    install -m 0644 ${S}/include/libjpeg-8b/*.h ${D}${includedir}/libjpeg-8b-tegra/
}

PACKAGES = "${PN}-dev ${PN}"
ALLOW_EMPTY_${PN} = "1"
