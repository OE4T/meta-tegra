DESCRIPTION = "NVIDIA Tegra Multimedia API headers"
HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

SRC_URI = "https://developer.download.nvidia.com/embedded/L4T/r31_Release_v1.0/BSP/Tegra_Multimedia_API_R${PV}_aarch64.tbz2"
SRC_URI[md5sum] = "65f1812bf594d8fe8346c2325cf930f6"
SRC_URI[sha256sum] = "cdf379e2a578d93b5c7b7ac6c7b89c545892eded23c5dbcfe48f490ea4a52478"

COMPATIBLE_MACHINE = "(tegra186|tegra194)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

DEPENDS = "tegra-libraries virtual/egl"

LIC_FILES_CHKSUM = "file://LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
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
