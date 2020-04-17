require recipes-bsp/tegra-binaries/tegra-binaries-${PV}.inc
require recipes-bsp/tegra-binaries/tegra-shared-binaries.inc

SRC_PKG_PATH  := "${S}/nv_tegra"
SRC_PKG_PATH[vardepvalue] = ""
S = "${WORKDIR}/graphics_demos"
B = "${S}"

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://include/GLES2/gl2ext_nv.h;endline=9;md5=fec8e0f064aa930eeb964b4149958073 \
                    file://include/EGL/eglext_nv.h;beginline=8;endline=16;md5=60d5636609dae48b08c5cdcef3d376c3"

do_unpack_from_tarfile() {
    tar -C ${WORKDIR} -x --strip-components=3 -f ${SRC_PKG_PATH}/graphics_demos.tbz2 usr/src/nvidia/graphics_demos/include
}
do_unpack_from_tarfile[dirs] = "${WORKDIR}"
do_unpack_from_tarfile[cleandirs] = "${S}"
do_unpack_from_tarfile[depends] += "tegra-binaries:do_preconfigure"

addtask unpack_from_tarfile before do_configure do_populate_lic

REQUIRED_DISTRO_FEATURES = "opengl"

do_install() {
    install -d ${D}${includedir}/EGL ${D}${includedir}/GLES2
    install -m 0644 ${S}/include/EGL/eglext_nv.h ${D}${includedir}/EGL/
    install -m 0644 ${S}/include/GLES2/gl2ext_nv.h ${D}${includedir}/GLES2/
}

PACKAGES = "${PN}-dev ${PN}"
ALLOW_EMPTY_${PN} = "1"
