TEGRA_UEFI_SIGNING_CLASS ??= "tegra-uefi-signing"

DT_NV_BASE = "${STAGING_DIR_HOST}/usr/src/device-tree/nvidia"

inherit devicetree
inherit ${TEGRA_UEFI_SIGNING_CLASS}

DEPENDS += "nvidia-kernel-oot"

# Overlay fragments combined into single DTBs contain some duplicate labels
DTC_FLAGS += "-E no-duplicate_label"

# From nvidia-kernel-oot build/nvidia-public/devicetree/Makefile.generic
DTC_PPFLAGS += "-DLINUX_VERSION=600 -DTEGRA_HOST1X_DT_VERSION=2 -DOS_LINUX"

# nv-public/ and nv-platform/ have same-named dtsi files; nv-public/ versions
# define the mmc aliases needed for correct block device enumeration and must
# come first. DT_FILES_PATH is appended last so recipes can override it.
DT_INCLUDE:tegra234 ?= " \
    ${DT_NV_BASE}/tegra/nv-public/include/kernel \
    ${DT_NV_BASE}/tegra/nv-public/include/nvidia-oot \
    ${DT_NV_BASE}/tegra/nv-public \
    ${DT_NV_BASE}/t23x/nv-public/include/nvidia-oot \
    ${DT_NV_BASE}/t23x/nv-public/include/platforms \
    ${DT_NV_BASE}/t23x/nv-public \
    ${DT_FILES_PATH} \
    ${KERNEL_INCLUDE} \
"
DT_INCLUDE:tegra264 ?= " \
    ${DT_NV_BASE}/tegra/nv-public/include/kernel \
    ${DT_NV_BASE}/tegra/nv-public/include/nvidia-oot \
    ${DT_NV_BASE}/tegra/nv-public \
    ${DT_NV_BASE}/t264/nv-public/include/kernel-t264 \
    ${DT_NV_BASE}/t264/nv-public \
    ${DT_FILES_PATH} \
    ${KERNEL_INCLUDE} \
"

# Re-implement expand_includes() from devicetree.bbclass to preserve the order
# of DT_INCLUDE; the OE-Core implementation uses set() which randomizes order.
def expand_includes(varname, d):
    import glob
    includes = list()
    for i in (d.getVar(varname) or "").split():
        for g in glob.glob(i):
            if os.path.isdir(g):
                includes.append(g)
    return includes

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

do_install() {
    devicetree_do_install
    for dtbf in ${B}/*.dtb.signed ${B}/*.dtbo.signed; do
        [ -e "$dtbf" ] || continue
        install -m 0644 "$dtbf" "${D}/boot/devicetree/"
    done
}

FILES:${PN} += "/boot/devicetree/*.dtb.signed /boot/devicetree/*.dtbo.signed"

do_deploy() {
    devicetree_do_deploy
    for dtbf in ${B}/*.dtb.signed ${B}/*.dtbo.signed; do
        [ -e "$dtbf" ] || continue
        install -m 0644 "$dtbf" "${DEPLOYDIR}/devicetree/"
    done
}
