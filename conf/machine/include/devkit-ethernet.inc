# 10G Ethernet (Orin AGX only) built as module in kernel cache
LINUX_ETHERNET:tegra234 = "kernel-module-aquantia"
NVIDIA_ETHERNET:tegra234 = ""

LINUX_ETHERNET:tegra264 = "kernel-module-r8169 kernel-module-realtek linux-firmware-rtl-nic"
NVIDIA_ETHERNET:tegra264 = "kernel-module-r8126"

DEVKIT_ETHERNET_RRECOMMENDS ?= "${@'${NVIDIA_ETHERNET}' if d.getVar('PREFERRED_PROVIDER_virtual/kernel') == 'linux-noble-nvidia-tegra' else '${LINUX_ETHERNET}'}"

MACHINE_EXTRA_RRECOMMENDS += "${DEVKIT_ETHERNET_RRECOMMENDS}"
