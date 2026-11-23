DESCRIPTION = "NVIDIA Jetson Linux device tree overlays"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

inherit tegra-devicetree

PROVIDES = "virtual/dtbo"

COMPATIBLE_MACHINE = "(tegra)"

DT_RESERVED_MAP = "0"
DT_PADDING_SIZE = "0"

DT_FILES_PATH:tegra234 = "${DT_NV_BASE}/t23x/nv-public/overlay"
DT_FILES_PATH:tegra264 = "${DT_NV_BASE}/t264/nv-public/overlay"
