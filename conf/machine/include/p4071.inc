# Peripheral configuration for p4071-0000 carrier (AGX Thor dev kit)
MACHINE_FEATURES += "wifi bluetooth"

LINUX_ETHERNET = "kernel-module-r8169 kernel-module-realtek linux-firmware-rtl-nic"
NVIDIA_ETHERNET = "kernel-module-r8126"

DEVKIT_ETHERNET_RRECOMMENDS ?= "${@'${NVIDIA_ETHERNET}' if d.getVar('TEGRA_USING_VENDOR_KERNEL') else '${LINUX_ETHERNET}'}"

LINUX_WIFI = "kernel-module-rtw89-8852ce linux-firmware-rtl8852"
NVIDIA_WIFI = "kernel-module-rtl8852ce tegra-firmware-rtl8852"

DEVKIT_WIFI_RRECOMMENDS ?= "${@'${NVIDIA_WIFI}' if d.getVar('TEGRA_USING_VENDOR_KERNEL') else '${LINUX_WIFI}'}"

MACHINE_EXTRA_RRECOMMENDS += " \
    ${DEVKIT_ETHERNET_RRECOMMENDS} \
    kernel-module-rtk-btusb \
    ${DEVKIT_WIFI_RRECOMMENDS} \
    kernel-module-snd-soc-rl6231 \
    kernel-module-snd-soc-rt5640 \
"
TEGRA_BT_SUPPORT_PACKAGE ?= ""
