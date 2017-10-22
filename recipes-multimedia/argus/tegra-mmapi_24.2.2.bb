DESCRIPTION = "NVIDIA Tegra Multimedia API headers"
HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

SRC_URI = "http://developer.download.nvidia.com/embedded/L4T/r24_Release_v2.2/Tegra_Multimedia_API_R${PV}_aarch64.tbz2"
SRC_URI[md5sum] = "5e6fa9a39b20e07c2d4da3647a13a647"
SRC_URI[sha256sum] = "78367e2fb15adf460fe8cc8cb8d4f0f2e166abb61e16ea9bbad707692928276c"

COMPATIBLE_MACHINE = "(tegra210)"
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
