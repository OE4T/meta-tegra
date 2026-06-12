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

NV_OVERLAY_PATH:tegra234 = "${DT_NV_BASE}/t23x/nv-public/overlay"
NV_OVERLAY_PATH:tegra264 = "${DT_NV_BASE}/t264/nv-public/overlay"

python do_compile:append() {
    import os
    overlay_path = d.getVar("NV_OVERLAY_PATH") or ""
    if not overlay_path or not os.path.isdir(overlay_path):
        return
    includes = expand_includes("DT_INCLUDE", d)
    for dts in sorted(os.listdir(overlay_path)):
        if not dts.endswith(".dts"):
            continue
        devicetree_compile(os.path.join(overlay_path, dts), includes, d)
}
