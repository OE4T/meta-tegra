FILESEXTRAPATHS:prepend:tegra := "${THISDIR}/${PN}:"

TEGRA_UEFI_SIGNING_CLASS ??= "tegra-uefi-signing"

INHERIT_DEPS = ""
INHERIT_DEPS:tegra = "${TEGRA_UEFI_SIGNING_CLASS}"
inherit ${INHERIT_DEPS}

SRC_URI:append:tegra = " \
    file://0001-memory-tegra-Add-Tegra234-clients-for-RCE-and-VI.patch \
    file://0002-hwmon-ina3221-Add-support-for-channel-summation-disa.patch \
    file://0003-cpufreq-tegra194-save-CPU-data-to-avoid-repeated-SMP.patch \
    file://0004-cpufreq-tegra194-use-refclk-delta-based-loop-instead.patch \
    file://0005-cpufreq-tegra194-remove-redundant-AND-with-cpu_onlin.patch \
    file://0006-fbdev-simplefb-Support-memory-region-property.patch \
    file://0007-fbdev-simplefb-Add-support-for-generic-power-domains.patch \
    file://0008-UBUNTU-SAUCE-PCI-endpoint-Add-core_deinit-callback-s.patch \
    file://tegra.cfg \
    file://tegra-drm.cfg \
    file://tegra-governors.cfg \
    file://tegra-pcie.cfg \
    file://tegra-scsi-ufs.cfg \
    file://tegra-sound.cfg \
    file://tegra-usb.cfg \
    file://tegra-v4l2.cfg \
    file://tegra-virtualization.cfg \
    file://rtw8822ce-wifi.cfg \
"

COMPATIBLE_MACHINE:tegra = "(tegra)"
KMACHINE:tegra = "genericarm64"

KERNEL_FEATURES:append:tegra = " \
    features/bluetooth/bluetooth.scc \
    features/bluetooth/bluetooth-usb.scc \
    features/i2c/i2c.scc \
    features/input/touchscreen.scc \
    features/media/media.scc \
    features/media/media-platform.scc \
    features/media/media-usb-webcams.scc \
    features/usb/serial.scc \
    features/usb/usb-raw-gadget.scc \
    features/usb/xhci-hcd.scc \
"

# All of our device trees are out-of-tree
KERNEL_DEVICETREE:forcevariable:tegra = ""

sign_kernel_image() {
    tegra_uefi_sbsign "$1"
    shift
    while [ $# -gt 0 ]; do
        tegra_uefi_attach_sign "$1"
        shift
    done
}

SIGNING_DEPS ?= ""
SIGNING_DEPS:tegra ?= "${TEGRA_UEFI_SIGNING_TASKDEPS}"

do_sign_kernel() {
    # do nothing by default
}
do_sign_kernel:tegra() {
    sign_kernel_image ${KERNEL_OUTPUT_DIR}/${KERNEL_IMAGETYPE}
}
do_sign_kernel[dirs] = "${B}"
do_sign_kernel[depends] += "${SIGNING_DEPS}"

addtask sign_kernel after do_compile before do_install

sign_bootimg() {
    tegra_uefi_attach_sign "$1"
}

bootimg_from_bundled_initramfs() {
    if [ ! -z "${INITRAMFS_IMAGE}" -a "${INITRAMFS_IMAGE_BUNDLE}" = "1" ]; then
        rm -f ${WORKDIR}/initrd
        touch ${WORKDIR}/initrd
        for imageType in ${KERNEL_IMAGETYPES} ; do
            if [ "$imageType" = "fitImage" ] ; then
                continue
            fi
            initramfs_base_name=${imageType}-${INITRAMFS_NAME}
            initramfs_symlink_name=${imageType}-${INITRAMFS_LINK_NAME}
            ${STAGING_BINDIR_NATIVE}/tegra-flash/mkbootimg \
                                    --kernel $deployDir/${initramfs_base_name}.bin \
                                    --ramdisk ${WORKDIR}/initrd \
                                    --cmdline "${KERNEL_ARGS}" \
                                    --output $deployDir/${initramfs_base_name}.cboot
            sign_bootimg $deployDir/${initramfs_base_name}.cboot
            chmod 0644 $deployDir/${initramfs_base_name}.cboot
            ln -sf ${initramfs_base_name}.cboot $deployDir/${initramfs_symlink_name}.cboot
        done
    elif [  -z "${INITRAMFS_IMAGE}" ]; then
        rm -f ${WORKDIR}/initrd
        touch ${WORKDIR}/initrd
        for imageType in ${KERNEL_IMAGETYPES} ; do
            if [ "$imageType" = "fitImage" ] ; then
                continue
            fi
            baseName=$imageType-${KERNEL_IMAGE_NAME}
            ${STAGING_BINDIR_NATIVE}/tegra-flash/mkbootimg \
                                    --kernel $deployDir/${baseName}.bin \
                                    --ramdisk ${WORKDIR}/initrd \
                                    --cmdline "${KERNEL_ARGS}" \
                                    --output $deployDir/${baseName}.cboot
            sign_bootimg $deployDir/${baseName}.cboot
            chmod 0644 $deployDir/${baseName}.cboot
            ln -sf ${baseName}.cboot $deployDir/$imageType-${KERNEL_IMAGE_LINK_NAME}.cboot
            ln -sf ${baseName}.cboot $deployDir/$imageType.cboot
        done
    fi
}
do_deploy:append:tegra() {
    bootimg_from_bundled_initramfs
}

DEPLOY_DEPS ?= ""
DEPLOY_DEPS:tegra ?= "tegra-flashtools-native:do_populate_sysroot ${TEGRA_UEFI_SIGNING_TASKDEPS}"
do_deploy[depends] += "${DEPLOY_DEPS}"

# RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base:tegra = ""

# kernel.bbclass automatically adds a dependency on the intramfs image
# even if INITRAMFS_IMAGE_BUNDLE is disabled.  This creates a circular
# dependency for tegra builds, where we need to combine initramfs (as an
# initrd) and kernel artifacts into a bootable image, so break that
# dependency here.
python () {
    mach_overrides = d.getVar('MACHINEOVERRIDES')
    mach_overrides = frozenset(mach_overrides.split(':'))

    if 'tegra' in mach_overrides and d.getVar('INITRAMFS_IMAGE') and not bb.utils.to_boolean(d.getVar('INITRAMFS_IMAGE_BUNDLE')):
        flags = d.getVarFlag('do_bundle_initramfs', 'depends', False).split()
        try:
            i = flags.index('${INITRAMFS_IMAGE}:do_image_complete')
            del flags[i]
            d.setVarFlag('do_bundle_initramfs', 'depends', ' '.join(flags))
        except ValueError:
            bb.warn('did not find it in %s' % ','.join(flags))
            pass
}
