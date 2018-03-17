DESCRIPTION = "NVIDIA Tegra Multimedia API headers"
HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/3.2GA/m892ki/JetPackL4T_32_b196/Tegra_Multimedia_API_R${PV}_aarch64.tbz2"
SRC_URI[md5sum] = "8fd01257b3102f406498f534eddbb313"
SRC_URI[sha256sum] = "66d80fd782063914e29776d3e341a81e8c7863b5c173cef01d5bf58ad03e1f92"
COMPATIBLE_MACHINE = "(tegra186|tegra210)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

PR = "r0"

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
}

PACKAGES = "${PN}-dev ${PN}"
ALLOW_EMPTY_${PN} = "1"
