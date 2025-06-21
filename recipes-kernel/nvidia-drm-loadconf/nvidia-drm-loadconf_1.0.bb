DESCRIPTION = "Adds a modprobe and modules load config for nvidia drm"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://nvidia-drm-modprobe.conf.in \
"

S = "${UNPACKDIR}"

COMPATIBLE_MACHINE = "(tegra234)"

# enable modesetting and fbdev support by default
NVIDIA_DRM_MODESET ?= "1"
NVIDIA_DRM_FBDEV ?= "1"

do_compile() {
    sed -e 's,@MODESET@,${NVIDIA_DRM_MODESET},' \
        -e 's,@FBDEV@,${NVIDIA_DRM_FBDEV},' \
        ${S}/nvidia-drm-modprobe.conf.in > ${B}/nvidia-drm-modprobe.conf
}

do_install() {
    install -d ${D}${sysconfdir}/modprobe.d ${D}${sysconfdir}/modules-load.d
    install -m 0644 ${B}/nvidia-drm-modprobe.conf ${D}${sysconfdir}/modprobe.d/nvidia-drm.conf
    echo "nvidia-drm" > ${D}${sysconfdir}/modules-load.d/nvidia-drm.conf
}

PACKAGES =+ "${PN}-modeset"
FILES:${PN}-modeset = "${sysconfdir}/modprobe.d"
RRECOMMENDS:${PN} = "${PN}-modeset"

# if modesetting was enabled at build time, don't include the modprobe conf at runtime alongside nvidia's XSERVER packages
RCONFLICTS:${PN}-modeset = "xserver-xorg-video-nvidia"
