DESCRIPTION = "NVIDIA Jetson-specific EGL and GLES2 extension headers"
HOMEPAGE = "https://developer.nvidia.com/embedded"

require l4t-graphics-demos.inc

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://include/GLES2/gl2ext_nv.h;endline=9;md5=fec8e0f064aa930eeb964b4149958073 \
                    file://include/EGL/eglext_nv.h;beginline=8;endline=16;md5=9f7d8800f2854b13b429ad32bd3ab7e1"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

do_install() {
    install -d ${D}${includedir}/EGL ${D}${includedir}/GLES2
    install -m 0644 ${S}/include/EGL/eglext_nv.h ${D}${includedir}/EGL/
    install -m 0644 ${S}/include/GLES2/gl2ext_nv.h ${D}${includedir}/GLES2/
}

PACKAGES = "${PN}-dev ${PN}"
ALLOW_EMPTY:${PN} = "1"
