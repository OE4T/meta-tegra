SECTION = "kernel"
SUMMARY = "Linux for Tegra kernel recipe"
DESCRIPTION = "Linux kernel from sources provided by Nvidia for Tegra processors."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit l4t_bsp
require recipes-kernel/linux/linux-yocto.inc

KERNEL_INTERNAL_WIRELESS_REGDB ?= "${@bb.utils.contains('DISTRO_FEATURES', 'wifi', '1', '0', d)}"
KERNEL_DISABLE_FW_USER_HELPER ?= "y"

DEPENDS:remove = "kern-tools-native"
DEPENDS:append = " kern-tools-tegra-native"
DEPENDS:append = "${@' wireless-regdb-native' if bb.utils.to_boolean(d.getVar('KERNEL_INTERNAL_WIRELESS_REGDB')) else ''}"

LINUX_VERSION ?= "4.9.337"
PV = "${LINUX_VERSION}+git${SRCPV}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}-${@bb.parse.vars_from_file(d.getVar('FILE', False),d)[1]}:"

LINUX_VERSION_EXTENSION ?= "-l4t-r${L4T_VERSION}"
SCMVERSION ??= "y"

SRCBRANCH = "oe4t-patches${LINUX_VERSION_EXTENSION}"
SRCREV = "d4116ecb5c13fe3136840fc409c8018c090ebaed"
KBRANCH = "${SRCBRANCH}"
SRC_REPO = "github.com/OE4T/linux-tegra-4.9;protocol=https"
KERNEL_REPO = "${SRC_REPO}"
SRC_URI = "git://${KERNEL_REPO};name=machine;branch=${KBRANCH} \
           ${@'file://localversion_auto.cfg' if d.getVar('SCMVERSION') == 'y' else ''} \
           ${@'file://disable-fw-user-helper.cfg' if d.getVar('KERNEL_DISABLE_FW_USER_HELPER') == 'y' else ''} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://systemd.cfg', '', d)} \
           ${@'file://wireless_regdb.cfg' if d.getVar('KERNEL_INTERNAL_WIRELESS_REGDB') == '1' else ''} \
"

PATH:prepend = "${STAGING_BINDIR_NATIVE}/kern-tools-tegra:"

KBUILD_DEFCONFIG = "tegra_defconfig"
KCONFIG_MODE = "--alldefconfig"

set_scmversion() {
    if [ "${SCMVERSION}" = "y" -a -d "${S}/.git" ]; then
        head=$(git --git-dir=${S}/.git rev-parse --verify --short HEAD 2>/dev/null || true)
        [ -z "$head" ] || echo "+g$head" > ${S}/.scmversion
    fi
}
do_kernel_checkout[postfuncs] += "set_scmversion"

python do_kernel_configcheck:prepend() {
    os.environ['KERNEL_OVERLAYS'] = d.expand("${S}/nvidia ${S}/nvidia/nvgpu")
}

KERNEL_DEVICETREE_APPLY_OVERLAYS ??= ""

overlay_compatible() {
	for oc in $1; do
		if echo "$2 " | grep -q "${oc} "; then
			return 0
		fi
	done
	return 1
}

do_compile_devicetree_overlays() {
	if [ -n "${KERNEL_DTC_FLAGS}" ]; then
		export DTC_FLAGS="${KERNEL_DTC_FLAGS}"
	fi
	oe_runmake dtb-overlays CC="${KERNEL_CC} $cc_extra " LD="${KERNEL_LD}" ${KERNEL_EXTRA_ARGS}
	# Prune out the overlays that are not compatible with at least
	# one of our device trees
	for dtbo in ${B}/arch/arm64/boot/dts/*.dtbo; do
		overlaycompat=$(fdtget "$dtbo" / compatible 2>/dev/null || echo "")
		keep=no
		if [ -n "$overlaycompat" ]; then
			for dtbf in ${KERNEL_DEVICETREE}; do
				dtb=$(get_real_dtb_path_in_kernel $(normalize_dtb "$dtbf"))
				compat=$(fdtget "$dtb" / compatible)
				if overlay_compatible "$overlaycompat" "$compat"; then
					keep=yes
					break
				fi
			done
		fi
		[ "$keep" = "yes" ] || rm -f "$dtbo"
	done
}
do_compile_devicetree_overlays[dirs] = "${B}"
do_compile_devicetree_overlays[depends] += "dtc-native:do_populate_sysroot"

addtask compile_devicetree_overlays after do_compile_kernelmodules before do_install

do_apply_devicetree_overlays() {

	[ -n "${KERNEL_DEVICETREE_APPLY_OVERLAYS}" ] || return 0

	for dtbf in ${KERNEL_DEVICETREE}; do
		dtb=$(get_real_dtb_path_in_kernel $(normalize_dtb "$dtbf"))
		compat=$(fdtget "$dtb" / compatible)
		overlayfiles=
		for dtbof in ${KERNEL_DEVICETREE_APPLY_OVERLAYS}; do
			dtbo=$(get_real_dtb_path_in_kernel $(normalize_dtb "$dtbof"))
			overlaycompat=$(fdtget "$dtbo" / compatible)
			if overlay_compatible "$overlaycompat" "$compat"; then
				overlayfiles="$overlayfiles $dtbo"
			fi
		done
		if [ -n "$overlayfiles" ]; then
			fdtoverlay --input "$dtb" --output "${dtb}.tmp" $overlayfiles
			rm "$dtb"
			mv "${dtb}.tmp" "$dtb"
		else
			bbnote "Skipping overlays for $dtb: none compatible"
		fi
	done
}
do_apply_devicetree_overlays[dirs] = "${B}"
do_apply_devicetree_overlays[depends] += "dtc-native:do_populate_sysroot"

do_install:append() {
	for dtbo in $(find ${KERNEL_OUTPUT_DIR}/dts/*.dtbo); do
		dtbo_base_name=`basename $dtbo .$dtbo_ext`
		install -m 0644 $dtbo ${D}/${KERNEL_IMAGEDEST}/$dtbo_base_name
	done
}

addtask apply_devicetree_overlays after do_compile_devicetree_overlays before do_install

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
                                    --cmdline "${KERNEL_ARGS}" \
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
                                    --cmdline "${KERNEL_ARGS}" \
                                    --output $deployDir/${baseName}.cboot
            chmod 0644 $deployDir/${baseName}.cboot
            ln -sf ${baseName}.cboot $deployDir/$imageType-${KERNEL_IMAGE_LINK_NAME}.cboot
            ln -sf ${baseName}.cboot $deployDir/$imageType.cboot
        done
    fi
}
do_deploy:append:tegra186() {
    bootimg_from_bundled_initramfs
}
do_deploy:append:tegra194() {
    bootimg_from_bundled_initramfs
}

EXTRADEPLOYDEPS = ""
EXTRADEPLOYDEPS:tegra186 = "tegra186-flashtools-native:do_populate_sysroot"
EXTRADEPLOYDEPS:tegra194 = "tegra186-flashtools-native:do_populate_sysroot"
do_deploy[depends] += "${EXTRADEPLOYDEPS}"

COMPATIBLE_MACHINE = "(tegra)"

RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base = "${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else '${KERNEL_PACKAGE_NAME}-image'}"

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

SRCTREECOVEREDTASKS += "do_kernel_add_regdb"
do_kernel_add_regdb() {
    if [ "${KERNEL_INTERNAL_WIRELESS_REGDB}" = "1" ]; then
        cp ${STAGING_LIBDIR_NATIVE}/crda/db.txt ${S}/net/wireless/db.txt
    fi
}
do_kernel_add_regdb[dirs] = "${S}"
addtask kernel_add_regdb before do_compile after do_configure
