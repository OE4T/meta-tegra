DESCRIPTION = "NVIDIA-specific OpenGL headers"
HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary"

SRC_URI = "https://developer.download.nvidia.com/embedded/L4T/r31_Release_v1.0/BSP/Tegra_Multimedia_API_R${PV}_aarch64.tbz2"
SRC_URI[md5sum] = "65f1812bf594d8fe8346c2325cf930f6"
SRC_URI[sha256sum] = "cdf379e2a578d93b5c7b7ac6c7b89c545892eded23c5dbcfe48f490ea4a52478"

LIC_FILES_CHKSUM = "file://include/EGL/eglext_nv.h;beginline=8;endline=16;md5=60d5636609dae48b08c5cdcef3d376c3"

S = "${WORKDIR}/tegra_multimedia_api"
B = "${S}"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}${includedir}/EGL ${D}${includedir}/GLES2
    install -m 0644 ${S}/include/EGL/eglext_nv.h ${D}${includedir}/EGL/
    install -m 0644 ${S}/include/GLES2/gl2ext_nv.h ${D}${includedir}/GLES2/
}

PACKAGES = "${PN}-dev ${PN}"
ALLOW_EMPTY_${PN} = "1"
