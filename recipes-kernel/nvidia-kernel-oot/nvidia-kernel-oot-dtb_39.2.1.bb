DESCRIPTION = "NVIDIA Jetson Linux device trees"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

inherit tegra-devicetree

COMPATIBLE_MACHINE = "(tegra)"

# Additional reserved map entries and padding aren't harmful, but aren't used in the L4T DTBs
DT_RESERVED_MAP = "0"
DT_PADDING_SIZE = "0"

DT_FILES_PATH:tegra234 = "${DT_NV_BASE}/t23x/nv-public/nv-platform"
DT_FILES_PATH:tegra264 = "${DT_NV_BASE}/t264/nv-public/nv-platform"
