DESCRIPTION = "NVIDIA-specific OpenGL headers"
HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary"

SRC_URI = "https://developer.download.nvidia.com/embedded/L4T/r31_Release_v0.1/EA/Tegra_Multimedia_API_R${PV}_aarch64.tbz2"
SRC_URI[md5sum] = "680c177242fc65ea398b62d99b81f49c"
SRC_URI[sha256sum] = "ca7ffcfe3681081c131c004b8f621022c82eb88d8039475881ecb82a8000762d"

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
