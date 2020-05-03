DESCRIPTION = "Generates a bootloader update payload for use with nv_update_engine when using a kernel with bundled initramfs"
LICENSE = "MIT"

COMPATIBLE_MACHINE = "(tegra186|tegra194)"

INHIBIT_DEFAULT_DEPS = "1"

inherit nopackages image_types_tegra deploy kernel-artifact-names

deltask do_fetch
deltask do_unpack
deltask do_patch
deltask do_configure
deltask do_compile
deltask do_install
deltask do_populate_sysroot

do_deploy() {
    if [ ! -z "${INITRAMFS_IMAGE}" -a "${INITRAMFS_IMAGE_BUNDLE}" = "1" ]; then
        for imageType in ${KERNEL_IMAGETYPES} ; do
	    if [ "$imageType" = "fitImage" ] ; then
	        continue
	    fi
	    initramfs_symlink_name=${imageType}-${INITRAMFS_LINK_NAME}
	    oe_make_bup_payload ${DEPLOY_DIR_IMAGE}/${initramfs_symlink_name}.cboot
	    install -d ${DEPLOYDIR}
	    install -m 0644 ${WORKDIR}/bup-payload/bl_update_payload ${DEPLOYDIR}/${initramfs_symlink_name}.bup-payload
	    for f in ${WORKDIR}/bup-payload/*_only_payload; do
		[ -e $f ] || continue
		sfx=$(basename $f _payload)
		install -m 0644 $f ${DEPLOYDIR}/${initramfs_symlink_name}.$sfx.bup-payload
	    done
	done
    fi
}
do_deploy[depends] += "virtual/kernel:do_deploy ${SOC_FAMILY}-flashtools-native:do_populate_sysroot dtc-native:do_populate_sysroot"
do_deploy[depends] += "tegra-redundant-boot-base:do_populate_sysroot tegra-bootfiles:do_populate_sysroot"
do_deploy[depends] += "coreutils-native:do_populate_sysroot cboot:do_deploy virtual/secure-os:do_deploy"
addtask deploy before do_build

PACKAGE_ARCH = "${MACHINE_ARCH}"
