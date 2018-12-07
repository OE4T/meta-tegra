DESCRIPTION = "Wayland EGL external platform library"
HOMEPAGE = "https://github.com/NVIDIA/egl-wayland"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cfa5a0f49cb081823fc5d965566e8298"
DEPENDS = "eglexternalplatform mesa wayland wayland-protocols wayland-native"

SRC_URI = "https://github.com/NVIDIA/egl-wayland/archive/${PV}.tar.gz;downloadfilename=${BP}.tar.gz"
SRC_URI[md5sum] = "d01b82530200bc5ad77283ff331f7439"
SRC_URI[sha256sum] = "d73a6344e766ca0cb0d3b3547173b1d7066f9dddc85e689f1b0b3fe466f54b51"
SRC_URI += " \
    file://0001-Fix-wayland-interface-checks.patch \
    file://nvidia_wayland.json \
"

inherit meson pkgconfig

do_install_append() {
    install -d ${D}${datadir}/egl/egl_external_platform.d
    install -m 0644 ${WORKDIR}/nvidia_wayland.json ${D}${datadir}/egl/egl_external_platform.d/
}

FILES_${PN} += "${datadir}/egl"
FILES_${PN}-dev += "${datadir}/wayland-eglstream"


