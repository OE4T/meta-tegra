DESCRIPTION = "NVIDIA Tegra Multimedia API headers"
HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

SRC_URI = "https://developer.nvidia.com/embedded/L4T/r28_Release_v4.0/t186ref_release_aarch64/Tegra_Multimedia_API_R28.4.0_aarch64.tbz2"
SRC_URI[md5sum] = "c20110edccd47dcd73c9dd7c3e0834a0"
SRC_URI[sha256sum] = "33467c9fbec4670b303c491a7f56dfc6e4ea116b262fc2e2ce342e684c615f27"

COMPATIBLE_MACHINE = "tegra186"
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
