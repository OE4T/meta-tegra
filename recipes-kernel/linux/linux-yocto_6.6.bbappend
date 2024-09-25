FILESEXTRAPATHS:prepend:tegra := "${THISDIR}/${PN}:"

require ${@'tegra-kernel.inc' if 'tegra' in d.getVar('MACHINEOVERRIDES').split(':') else ''}

SRC_URI:append:tegra = " \
    file://0001-memory-tegra-Add-Tegra234-clients-for-RCE-and-VI.patch \
    file://0002-hwmon-ina3221-Add-support-for-channel-summation-disa.patch \
    file://0003-cpufreq-tegra194-save-CPU-data-to-avoid-repeated-SMP.patch \
    file://0004-cpufreq-tegra194-use-refclk-delta-based-loop-instead.patch \
    file://0005-cpufreq-tegra194-remove-redundant-AND-with-cpu_onlin.patch \
    file://0006-fbdev-simplefb-Support-memory-region-property.patch \
    file://0007-fbdev-simplefb-Add-support-for-generic-power-domains.patch \
    file://0008-UBUNTU-SAUCE-PCI-endpoint-Add-core_deinit-callback-s.patch \
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
