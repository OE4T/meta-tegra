DESCRIPTION = "NVIDIA-specific OpenGL headers"
HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary"

#SRC_URI = "https://developer.nvidia.com/embedded/L4T/r28_Release_v4.0/t186ref_release_aarch64/Tegra_Multimedia_API_R${PV}_aarch64.tbz2"
SRC_URI = "http://172.17.0.1/Tegra_Multimedia_API_R${PV}_aarch64.tbz2"
SRC_URI[md5sum] = "c20110edccd47dcd73c9dd7c3e0834a0"
SRC_URI[sha256sum] = "33467c9fbec4670b303c491a7f56dfc6e4ea116b262fc2e2ce342e684c615f27"

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
