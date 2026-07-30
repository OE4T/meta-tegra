# Peripheral configuration for p3768-0000 carrier (Orin NX/Nano dev kit)
MACHINE_FEATURES += "wifi bluetooth"

LINUX_WIFI = "kernel-module-rtw88-8822ce linux-firmware-rtl8822"
LINUX_ETHERNET = "kernel-module-r8169"
NVIDIA_WIFI = "nv-kernel-module-rtl8822ce"
NVIDIA_ETHERNET = "nv-kernel-module-r8168"

DEVKIT_WIFI_RRECOMMENDS ?= "${@'${NVIDIA_WIFI}' if d.getVar('TEGRA_USING_VENDOR_KERNEL') else '${LINUX_WIFI}'}"
DEVKIT_ETHERNET_RRECOMMENDS ?= "${@'${NVIDIA_ETHERNET}' if d.getVar('TEGRA_USING_VENDOR_KERNEL') else '${LINUX_ETHERNET}'} kernel-module-realtek linux-firmware-rtl8168"

MACHINE_EXTRA_RRECOMMENDS += " \
    nv-kernel-module-rtk-btusb \
    tegra-firmware-rtl8822 \
    ${DEVKIT_WIFI_RRECOMMENDS} \
    ${DEVKIT_ETHERNET_RRECOMMENDS} \
"

TEGRA_BT_SUPPORT_PACKAGE ?= ""
