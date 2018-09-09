DESCRIPTION = "NVIDIA-specific OpenGL headers"
HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary"

SRC_URI = "https://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/3.2.1/m8u2ki/JetPackL4T_321_b23/Tegra_Multimedia_API_R${PV}_aarch64.tbz2"
SRC_URI[md5sum] = "ffc676c2bfae02c74c7520e7b72ab757"
SRC_URI[sha256sum] = "cad2f36d5eb8be36984a0c1dcb4d8055c718b5f9da23ec52843cc48e47291466"

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
