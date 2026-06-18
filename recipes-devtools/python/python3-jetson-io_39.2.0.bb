SUMMARY = "Jetson expansion header configuration tool"
DESCRIPTION = "Python scripts for reconfiguring Jetson expansion headers via device tree overlays"
L4T_DEB_COPYRIGHT_MD5 = "01542784ed6e489cd77bae5aafcddd6c"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-jetson-io"

require recipes-bsp/tegra-binaries/tegra-debian-libraries-common.inc

inherit python3native

JETSON_IO_BASE = "/opt/nvidia/jetson-io"

SRC_URI += "file://0001-Jetson-board.py-fix-block-device-check-for-standard-.patch"

do_install() {
    install -d ${D}${JETSON_IO_BASE}/
    cp -R --no-dereference --preserve=links,mode,timestamps ${S}${JETSON_IO_BASE}/* ${D}${JETSON_IO_BASE}/
}

FILES:${PN} += "${JETSON_IO_BASE}"

RDEPENDS:${PN} = "\
    dtc \
    l4t-launcher-extlinux \
    python3 \
    util-linux-lsblk \
    util-linux-mount \
    util-linux-mountpoint \
"

RRECOMMENDS:${PN} = "l4t-launcher-extlinux-dtb-extra"

PACKAGE_ARCH = "${MACHINE_ARCH}"
