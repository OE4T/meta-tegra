DESCRIPTION = "NVIDIA Jetson Linux device trees"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS += "nvidia-kernel-oot"

TEGRA_UEFI_SIGNING_CLASS ??= "tegra-uefi-signing"

inherit devicetree
inherit ${TEGRA_UEFI_SIGNING_CLASS}

COMPATIBLE_MACHINE = "(tegra)"

# Overlay fragments combined into single DTBs contain some duplicate
# labels. DTC 1.7.0 treats this as a fatal error by default.
DTC_FLAGS:append = " -E no-duplicate_label"

DT_NV_BASE = "${STAGING_DIR_HOST}/usr/src/device-tree/nvidia"

DT_FILES_PATH:tegra234 = "${DT_NV_BASE}/t23x/nv-public/nv-platform"
DT_FILES_PATH:tegra264 = "${DT_NV_BASE}/t264/nv-public/nv-platform"

# The devicetree bbclass expand_includes() uses a Python set(), which loses
# include order, which is critical since there are some same-named headers.
# Prepend the NVIDIA extended kernel headers to DTC_PPFLAGS so those flags
# are emitted before DT_INCLUDE paths.
DTC_PPFLAGS:prepend:tegra234 = "-I${DT_NV_BASE}/tegra/nv-public/include/kernel "
DTC_PPFLAGS:prepend:tegra264 = "-I${DT_NV_BASE}/tegra/nv-public/include/kernel "

DT_INCLUDE:tegra234 = " \
    ${DT_NV_BASE}/t23x/nv-public/nv-platform \
    ${DT_NV_BASE}/t23x/nv-public \
    ${DT_NV_BASE}/t23x/nv-public/include/nvidia-oot \
    ${DT_NV_BASE}/t23x/nv-public/include/platforms \
    ${DT_NV_BASE}/tegra/nv-public \
    ${KERNEL_INCLUDE} \
"
DT_INCLUDE:tegra264 = " \
    ${DT_NV_BASE}/t264/nv-public/nv-platform \
    ${DT_NV_BASE}/t264/nv-public \
    ${DT_NV_BASE}/t264/nv-public/include/nvidia-oot \
    ${DT_NV_BASE}/t264/nv-public/include/platforms \
    ${DT_NV_BASE}/tegra/nv-public \
    ${KERNEL_INCLUDE} \
"

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

do_sign_dtbs() {
    for dtbf in ${B}/*.dtb ${B}/*.dtbo; do
        [ -e "$dtbf" ] || continue
        tegra_uefi_attach_sign "$dtbf"
    done
}
do_sign_dtbs[dirs] = "${B}"
do_sign_dtbs[depends] += "${TEGRA_UEFI_SIGNING_TASKDEPS}"
do_sign_dtbs[file-checksums] += "${TEGRA_UEFI_SIGNING_FILECHECKSUMS}"

addtask sign_dtbs after do_compile before do_install

do_install:append() {
    for dtbf in ${B}/*.dtb.signed ${B}/*.dtbo.signed; do
        [ -e "$dtbf" ] || continue
        install -m 0644 "$dtbf" "${D}/boot/devicetree/"
    done
}

FILES:${PN} += "/boot/devicetree/*.dtb.signed /boot/devicetree/*.dtbo.signed"
