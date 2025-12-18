FILESEXTRAPATHS:prepend:tegra := "${THISDIR}/${PN}-6.12:${THISDIR}/${PN}:"

require ${@'tegra-kernel.inc' if 'tegra' in d.getVar('MACHINEOVERRIDES').split(':') else ''}

SRC_URI:append:tegra = " \
    file://0001-NVIDIA-SAUCE-soc-tegra-pmc-Add-sysfs-nodes-to-select.patch \
    file://0002-UBUNTU-SAUCE-mtd-spi-nor-support-for-spansion-and-ma.patch \
    file://0003-NVIDIA-SAUCE-enable-handling-of-macronix-block-prote.patch \
    file://tegra.cfg \
    file://tegra-drm.cfg \
    file://tegra-governors.cfg \
    file://tegra-pcie.cfg \
    file://tegra-scsi-ufs.cfg \
    file://tegra-sound.cfg \
    file://tegra-usb.cfg \
    file://tegra-v4l2.cfg \
    file://tegra-virtualization.cfg \
    file://rtw8822ce-wifi.cfg \
    file://r8169.cfg \
"

COMPATIBLE_MACHINE:tegra = "(tegra)"
KMACHINE:tegra = "genericarm64"

KERNEL_FEATURES:append:tegra = " \
    features/bluetooth/bluetooth.scc \
    features/bluetooth/bluetooth-usb.scc \
    features/i2c/i2c.scc \
    features/input/touchscreen.scc \
    features/media/media.scc \
    features/media/media-platform.scc \
    features/media/media-usb-webcams.scc \
    features/usb/serial.scc \
    features/usb/usb-raw-gadget.scc \
    features/usb/xhci-hcd.scc \
"
