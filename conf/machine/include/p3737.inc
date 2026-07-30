# Peripheral configuration for p3737-0000 carrier (Orin AGX dev kit)
MACHINE_FEATURES += "wifi bluetooth"

LINUX_ETHERNET = "kernel-module-aquantia"
NVIDIA_ETHERNET = ""

DEVKIT_ETHERNET_RRECOMMENDS ?= "${@'${NVIDIA_ETHERNET}' if d.getVar('TEGRA_USING_VENDOR_KERNEL') else '${LINUX_ETHERNET}'}"

LINUX_WIFI = "kernel-module-rtw88-8822ce linux-firmware-rtl8822"
NVIDIA_WIFI = "kernel-module-rtl8822ce tegra-firmware-rtl8822"

DEVKIT_WIFI_RRECOMMENDS ?= "${@'${NVIDIA_WIFI}' if d.getVar('TEGRA_USING_VENDOR_KERNEL') else '${LINUX_WIFI}'}"

MACHINE_EXTRA_RRECOMMENDS += " \
    ${DEVKIT_ETHERNET_RRECOMMENDS} \
    kernel-module-rtk-btusb \
    ${DEVKIT_WIFI_RRECOMMENDS} \
    kernel-module-snd-soc-rl6231 \
    kernel-module-snd-soc-rt5640 \
"
TEGRA_BT_SUPPORT_PACKAGE ?= ""
