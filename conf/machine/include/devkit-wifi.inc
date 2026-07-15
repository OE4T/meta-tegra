MACHINE_FEATURES += "wifi bluetooth"

LINUX_WIFI:tegra264 = "kernel-module-rtw89-8852ce linux-firmware-rtl8852"
NVIDIA_WIFI:tegra264 = "kernel-module-rtl8852ce tegra-firmware-rtl8852"

LINUX_WIFI:tegra234 = "kernel-module-rtw88-8822ce linux-firmware-rtl8822"
NVIDIA_WIFI:tegra234 = "kernel-module-rtl8822ce tegra-firmware-rtl8822"

DEVKIT_WIFI_RRECOMMENDS ?= "${@'${NVIDIA_WIFI}' if d.getVar('PREFERRED_PROVIDER_virtual/kernel') == 'linux-noble-nvidia-tegra' else '${LINUX_WIFI}'}"

MACHINE_EXTRA_RRECOMMENDS += " \
    kernel-module-rtk-btusb \
    ${DEVKIT_WIFI_RRECOMMENDS} \
"
TEGRA_BT_SUPPORT_PACKAGE ?= ""
