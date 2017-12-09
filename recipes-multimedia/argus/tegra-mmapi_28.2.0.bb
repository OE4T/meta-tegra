DESCRIPTION = "NVIDIA Tegra Multimedia API headers"
HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/3.2/pwv346/JetPackL4T_32_b157/Tegra_Multimedia_API_R${PV}_aarch64.tbz2"
SRC_URI[md5sum] = "37a9ea02c920071fd1ca1f38162e23a0"
SRC_URI[sha256sum] = "537801eee0cd51657f729869a669c2618d5789f0fb7d05c2b43c06cab4135a20"
COMPATIBLE_MACHINE = "(tegra186)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

PR = "rc0"

DEPENDS = "tegra-libraries virtual/egl"

LIC_FILES_CHKSUM = "file://LICENSE;md5=60ad17cc726658e8cf73578bea47b85f \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra_multimedia_api"
B = "${S}"

do_configure() {
    rm -rf ${S}/include/EGL ${S}/include/GL* ${S}/include/KHR* ${S}/include/libv4l2.h
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
