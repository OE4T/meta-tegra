DESCRIPTION = "Generate an extlinux.conf for use with cboot"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(tegra194)"

DEPENDS = "tegra186-flashtools-native dtc-native"

UBOOT_EXTLINUX = "1"

inherit cboot-extlinux-config kernel-artifact-names

TEGRA_SIGNING_ARGS ??= ""
TEGRA_SIGNING_EXCLUDE_TOOLS ??= ""
TEGRA_SIGNING_EXTRA_DEPS ??= ""
KERNEL_ARGS ??= ""
DTBFILE ?= "${@os.path.basename(d.getVar('KERNEL_DEVICETREE').split()[0])}"

# Need to handle:
#  a) Kernel with no initrd/initramfs
#  b) Kernel with bundled initramfs
#  c) Kernel with separate initrd
def compute_dependencies(d):
    deps = "virtual/kernel:do_deploy"
    initramfs_image = d.getVar('INITRAMFS_IMAGE') or ''
    if initramfs_image != '' and (d.getVar('INITRAMFS_IMAGE_BUNDLE') or '') != '1':
        deps += " %s:do_image_complete" % initramfs_image
    return deps


PATH =. "${STAGING_BINDIR_NATIVE}/tegra186-flash:"

do_configure() {
    :
}

do_compile() {
    if [ -n "${INITRAMFS_IMAGE}" ]; then
        if [ "${INITRAMFS_IMAGE_BUNDLE}" = "1" ]; then
	    cp -L ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${INITRAMFS_LINK_NAME}.bin ${B}/${KERNEL_IMAGETYPE}
	else
	    cp -L ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.bin ${B}/${KERNEL_IMAGETYPE}
	    cp -L ${DEPLOY_DIR_IMAGE}/${INITRAMFS_IMAGE}-${MACHINE}.cpio.gz ${B}/initrd
	fi
    else
	cp -L ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.bin ${B}/${KERNEL_IMAGETYPE}
    fi
    if [ -n "${UBOOT_EXTLINUX_FDT}" ]; then
        cp -L ${DEPLOY_DIR_IMAGE}/${DTBFILE} ${B}/
        if [ -n "${KERNEL_ARGS}" ]; then
            fdtput -t s ${B}/${DTBFILE} /chosen bootargs "${KERNEL_ARGS}"
        elif fdtget -t s ${B}/${DTBFILE} /chosen bootargs >/dev/null 2>&1; then
            fdtput -d ${B}/${DTBFILE} /chosen bootargs
        fi
    fi
}
do_compile[depends] += "${@compute_dependencies(d)}"
do_compile[cleandirs] = "${B}"

# Override this function in a bbappend to handle alternative
# signing mechanisms
sign_files() {
    tegra-signimage-helper ${TEGRA_SIGNING_ARGS} --chip ${NVIDIA_CHIP} "$@"
}

do_sign_files() {
    files_to_sign="${KERNEL_IMAGETYPE}"
    if [ -n "${UBOOT_EXTLINUX_FDT}" ]; then
        files_to_sign="$files_to_sign ${DTBFILE}"
    fi
    if [ -n "${INITRAMFS_IMAGE}" -a "${INITRAMFS_IMAGE_BUNDLE}" != "1" ]; then
        files_to_sign="$files_to_sign initrd"
    fi
    sign_files $files_to_sign
}
do_sign_files[dirs] = "${B}"
do_sign_files[depends] += "${TEGRA_SIGNING_EXTRA_DEPS}"

addtask sign_files after do_create_extlinux_config before do_install

do_install() {
    install -d ${D}/boot/extlinux
    install -m 0644 ${B}/${KERNEL_IMAGETYPE} ${B}/${KERNEL_IMAGETYPE}.sig ${D}/boot/
    if [ -n "${UBOOT_EXTLINUX_FDT}" ]; then
        install -m 0644 ${B}/${DTBFILE} ${B}/${DTBFILE}.sig ${D}/boot/
    fi
    if [ -n "${INITRAMFS_IMAGE}" -a "${INITRAMFS_IMAGE_BUNDLE}" != "1" ]; then
        install -m 0644 ${B}/initrd ${B}/initrd.sig ${D}/boot/
    fi
    install -m 0644 ${B}/extlinux.conf ${D}/boot/extlinux/
}

FILES:${PN} = "/boot"
PACKAGE_ARCH = "${MACHINE_ARCH}"
