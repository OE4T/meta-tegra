DESCRIPTION = "NVIDIA Tegra Multimedia API headers"
HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

INCNAME = "tegra186"
INCNAME_tegra210 = "tegra210"
require tegra-mmapi-${INCNAME}-28.3.0.inc

COMPATIBLE_MACHINE = "(tegra186|tegra210)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

DEPENDS = "tegra-libraries virtual/egl"

LIC_FILES_CHKSUM = "file://LICENSE;md5=2174e6214d83da8e19ab6510ffa71336 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra_multimedia_api"
B = "${S}"

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
