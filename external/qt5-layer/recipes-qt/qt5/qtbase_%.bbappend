FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-eglfs-Newer-Nvidia-libdrm-provide-device-instead-dri.patch \
            file://0002-eglfs-add-a-default-framebuffer-to-NVIDIA-eglstreams.patch \
"

PACKAGECONFIG:append:tegra = " kms"
