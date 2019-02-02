DESCRIPTION = "Wayland EGL external platform library"
HOMEPAGE = "https://github.com/NVIDIA/egl-wayland"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cfa5a0f49cb081823fc5d965566e8298"
DEPENDS = "eglexternalplatform mesa wayland wayland-protocols wayland-native"

SRC_URI = "https://github.com/NVIDIA/egl-wayland/archive/${PV}.tar.gz;downloadfilename=${BP}.tar.gz"
SRC_URI[md5sum] = "c698a493f937988f2269c5e05f38b8b8"
SRC_URI[sha256sum] = "0970ad869845525f243ccfce8b45f740c35b7ce4327241e49a7cbe910fedc360"
SRC_URI += " \
    file://0001-Fix-wayland-interface-checks.patch \
    file://0002-Fix-wayland-eglstream-protocols-pc-file.patch \
    file://nvidia_wayland.json \
"

inherit meson pkgconfig

do_install_append() {
    install -d ${D}${datadir}/egl/egl_external_platform.d
    install -m 0644 ${WORKDIR}/nvidia_wayland.json ${D}${datadir}/egl/egl_external_platform.d/
}

FILES_${PN} += "${datadir}/egl"
FILES_${PN}-dev += "${datadir}/wayland-eglstream"


