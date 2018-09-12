FILESEXTRAPATHS_prepend := "${THISDIR}/${BP}:"

SRC_URI += " \
    file://0001-compositor-drm-Ignore-non-KMS-devices.patch \
    file://0002-gl-renderer-support-format-and-modifier-queries.patch \
    file://0003-gl-renderer-allow-importing-dmabufs-with-format-modi.patch \
    file://0004-gl-renderer-allow-importing-fourth-dmabuf-plane.patch \
    file://0005-Don-t-delay-initial-output-paint.patch \
    file://0006-gl-renderer-Add-EGLDevice-enumeration-support.patch \
    file://0007-gl-renderer-Add-support-for-EGLDevice-composited-fra.patch \
    file://0008-gl-renderer-Add-EGL-client-support-for-EGLStream-fra.patch \
    file://0009-compositor-drm-Gracefully-handle-vblank-and-flip-inv.patch \
    file://0010-compositor-drm-Add-support-for-EGLDevice-EGLOutput.patch \
    file://0011-simple-egl-Do-not-set-EGL-up-until-XDG-setup-is-comp.patch \
    file://0012-Add-nvidia-release-notes-file.patch \
    file://0013-compositor-drm-Release-current-next-fb-when-deactiva.patch \
    file://0014-compositor-Process-stream-attach-requests-with-wl_eg.patch \
    file://0015-libweston-have-compositor-drm-try-drmOpen-should-ude.patch \
    file://0016-updates-to-work-with-tegra-drm-nvdc.patch \
    file://weston.default \
"
LIC_FILES_CHKSUM = "file://COPYING;md5=d79ee9e66bb0f95d3386a7acae780b70 \
                    file://libweston/compositor.c;endline=26;md5=81392b0a7ef9469e4c5c28916c0e417a"

DEPENDS += "egl-wayland"
PACKAGECONFIG_remove_tegra = "fbdev"
PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"

do_install_append_tegra() {
    install -d ${D}${sysconfdir}/default
    install -m 0644 ${WORKDIR}/weston.default ${D}${sysconfdir}/default/weston
}
FILES_${PN} += "${sysconfdir}/default/weston"
RDEPENDS_${PN} += "egl-wayland"
