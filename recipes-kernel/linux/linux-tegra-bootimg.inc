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
            ${STAGING_BINDIR_NATIVE}/tegra186-flash/mkbootimg \
                                    --kernel $deployDir/${initramfs_base_name}.bin \
                                    --ramdisk ${WORKDIR}/initrd \
                                    --output $deployDir/${initramfs_base_name}.cboot
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
            ${STAGING_BINDIR_NATIVE}/tegra186-flash/mkbootimg \
                                    --kernel $deployDir/${baseName}.bin \
                                    --ramdisk ${WORKDIR}/initrd \
                                    --output $deployDir/${baseName}.cboot
            chmod 0644 $deployDir/${baseName}.cboot
            ln -sf ${baseName}.cboot $deployDir/$imageType-${KERNEL_IMAGE_LINK_NAME}.cboot
            ln -sf ${baseName}.cboot $deployDir/$imageType.cboot
        done
    fi
}
do_deploy_append_tegra186() {
    bootimg_from_bundled_initramfs
}
do_deploy_append_tegra194() {
    bootimg_from_bundled_initramfs
}

EXTRADEPLOYDEPS = ""
EXTRADEPLOYDEPS_tegra186 = "tegra186-flashtools-native:do_populate_sysroot"
EXTRADEPLOYDEPS_tegra194 = "tegra186-flashtools-native:do_populate_sysroot"
do_deploy[depends] += "${EXTRADEPLOYDEPS}"

COMPATIBLE_MACHINE = "(tegra)"

RDEPENDS_${KERNEL_PACKAGE_NAME}-base = "${@'${KERNEL_PACKAGE_NAME}-image' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('u-boot') else ''}"

# kernel.bbclass automatically adds a dependency on the intramfs image
# even if INITRAMFS_IMAGE_BUNDLE is disabled.  This creates a circular
# dependency for tegra builds, where we need to combine initramfs (as an
# initrd) and kernel artifacts into a bootable image, so break that
# dependency here.
python () {
    image = d.getVar('INITRAMFS_IMAGE')
    if image and not bb.utils.to_boolean(d.getVar('INITRAMFS_IMAGE_BUNDLE')):
        flags = d.getVarFlag('do_bundle_initramfs', 'depends', False).split()
        try:
            i = flags.index('${INITRAMFS_IMAGE}:do_image_complete')
            del flags[i]
            d.setVarFlag('do_bundle_initramfs', 'depends', ' '.join(flags))
        except ValueError:
            bb.warn('did not find it in %s' % ','.join(flags))
            pass
}
