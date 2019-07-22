FILESEXTRAPATHS_prepend := "${THISDIR}/${BP}:"

SRC_URI += " \
    file://0001-gl-renderer-Add-EGLDevice-enumeration-support.patch \
    file://0002-gl-renderer-Add-support-for-EGLDevice-composited-fra.patch \
    file://0003-gl-renderer-Add-EGL-client-support-for-EGLStream-fra.patch \
    file://0004-compositor-drm-Gracefully-handle-vblank-and-flip-inv.patch \
    file://0005-compositor-drm-Add-support-for-EGLDevice-EGLOutput.patch \
    file://0006-compositor-Process-stream-attach-requests-with-wl_eg.patch \
    file://0007-simple-egl-Do-not-set-EGL-up-until-XDG-setup-is-comp.patch \
    file://0008-gl-renderer-Use-mailbox-streams-for-scanout.patch \
    file://0009-libweston-have-compositor-drm-try-drmOpen-should-ude.patch \
    file://0010-updates-to-work-with-tegra-drm-nvdc.patch \
    file://0011-compositor-drm-pass-drm-nvdc-to-drmOpen.patch \
    file://weston.default \
"
LIC_FILES_CHKSUM = "file://COPYING;md5=d79ee9e66bb0f95d3386a7acae780b70 \
                    file://libweston/compositor.c;endline=28;md5=866f3381d3f7d9e7035d6b2c895fb668"

DEPENDS += "egl-wayland"
PACKAGECONFIG_remove_tegra = "fbdev"
PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"

do_install_append_tegra() {
    install -d ${D}${sysconfdir}/default
    install -m 0644 ${WORKDIR}/weston.default ${D}${sysconfdir}/default/weston
}
FILES_${PN} += "${sysconfdir}/default/weston"
RDEPENDS_${PN} += "egl-wayland"
