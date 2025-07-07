SECTION = "kernel"
SUMMARY = "Linux for Tegra kernel recipe"
DESCRIPTION = "Linux kernel from sources provided by Nvidia for Tegra processors."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

TEGRA_UEFI_SIGNING_CLASS ??= "tegra-uefi-signing"

inherit l4t_bsp python3native ${TEGRA_UEFI_SIGNING_CLASS}
require recipes-kernel/linux/linux-yocto.inc

KERNEL_DISABLE_FW_USER_HELPER ?= "y"

DEPENDS:remove = "kern-tools-native"
DEPENDS:append = " kern-tools-tegra-native"

LINUX_VERSION ?= "5.10.216"
PV = "${LINUX_VERSION}+git${SRCPV}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}-${@bb.parse.vars_from_file(d.getVar('FILE', False),d)[1]}:"

LINUX_VERSION_EXTENSION ?= "-l4t-r35.6.1"
SCMVERSION ??= "y"

SRCBRANCH = "oe4t-patches${LINUX_VERSION_EXTENSION}"
SRCREV = "4e110b95829d222e4a9059fe9dc691895cd5b5b9"
KBRANCH = "${SRCBRANCH}"
SRC_REPO = "github.com/OE4T/linux-tegra-5.10.git;protocol=https"
KERNEL_REPO = "${SRC_REPO}"
SRC_URI = "git://${KERNEL_REPO};name=machine;branch=${KBRANCH} \
           ${@'file://localversion_auto.cfg' if d.getVar('SCMVERSION') == 'y' else ''} \
           ${@'file://disable-fw-user-helper.cfg' if d.getVar('KERNEL_DISABLE_FW_USER_HELPER') == 'y' else ''} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://systemd.cfg', '', d)} \
	   file://spiflash.cfg \
	   file://disable-module-signing.cfg \
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
}
do_compile_devicetree_overlays[dirs] = "${B}"
do_compile_devicetree_overlays[depends] += "dtc-native:do_populate_sysroot"

addtask compile_devicetree_overlays after do_compile before do_install

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

addtask apply_devicetree_overlays after do_compile_devicetree_overlays before do_install

sign_kernel_image_and_dtb_files() {
    tegra_uefi_sbsign "$1"
    shift
    while [ $# -gt 0 ]; do
        tegra_uefi_attach_sign "$1"
        shift
    done
}

do_sign_kernel_and_dtbs() {
    local dtb dtbf alldtbs
    alldtbs=""
    for dtbf in ${KERNEL_DEVICETREE}; do
        dtb=$(get_real_dtb_path_in_kernel $(normalize_dtb "$dtbf"))
	alldtbs="$alldtbs $dtb"
    done
    sign_kernel_image_and_dtb_files ${KERNEL_OUTPUT_DIR}/${KERNEL_IMAGETYPE} $alldtbs
}
do_sign_kernel_and_dtbs[dirs] = "${B}"
do_sign_kernel_and_dtbs[depends] += "${TEGRA_UEFI_SIGNING_TASKDEPS}"

addtask sign_kernel_and_dtbs after do_compile before do_install

do_install:append() {
	for dtbo in $(find ${KERNEL_OUTPUT_DIR}/dts/*.dtbo); do
		dtbo_base_name=$(basename $dtbo)
		install -m 0644 $dtbo ${D}/${KERNEL_IMAGEDEST}/$dtbo_base_name
	done
}

OVERLAYS_TO_DEPLOY = '${TEGRA_PLUGIN_MANAGER_OVERLAYS} ${@" ".join((d.getVar("OVERLAY_DTB_FILE") or "").split(","))}'
do_deploy:append() {
	for dtbo in ${OVERLAYS_TO_DEPLOY}; do
		if [ -e ${KERNEL_OUTPUT_DIR}/dts/$dtbo ]; then
			install -m 0644 ${KERNEL_OUTPUT_DIR}/dts/$dtbo $deployDir
		fi
	done
	for dtbf in ${KERNEL_DEVICETREE}; do
		dtb=$(normalize_dtb "$dtbf")
		dtb_ext=${dtb##*.}
		dtb_path=$(get_real_dtb_path_in_kernel "$dtb")
		dtb_base_name=$(basename $dtb .$dtb_ext)
		if [ -e $dtb_path.signed ] ; then
			install -m 0644 $dtb_path.signed $deployDir/$dtb_base_name-${KERNEL_DTB_NAME}.$dtb_ext.signed
			if [ "${KERNEL_IMAGETYPE_SYMLINK}" = "1" ] ; then
				ln -sf $dtb_base_name-${KERNEL_DTB_NAME}.$dtb_ext.signed $deployDir/$dtb_base_name.$dtb_ext.signed
			fi
			if [ -n "${KERNEL_DTB_LINK_NAME}" ] ; then
				ln -sf $dtb_base_name-${KERNEL_DTB_NAME}.$dtb_ext.signed $deployDir/$dtb_base_name-${KERNEL_DTB_LINK_NAME}.$dtb_ext.signed
			fi
		fi
	done
}

sign_bootimg() {
    tegra_uefi_attach_sign "$1"
    rm "$1"
    mv "$1.signed" "$1"
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
                                    --cmdline '${KERNEL_ARGS}' \
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
                                    --cmdline '${KERNEL_ARGS}' \
                                    --output $deployDir/${baseName}.cboot
	    sign_bootimg $deployDir/${initramfs_base_name}.cboot
            chmod 0644 $deployDir/${baseName}.cboot
            ln -sf ${baseName}.cboot $deployDir/$imageType-${KERNEL_IMAGE_LINK_NAME}.cboot
            ln -sf ${baseName}.cboot $deployDir/$imageType.cboot
        done
    fi
}
do_deploy:append() {
    bootimg_from_bundled_initramfs
}

do_deploy[depends] += "tegra-flashtools-native:do_populate_sysroot ${TEGRA_UEFI_SIGNING_TASKDEPS}"

COMPATIBLE_MACHINE = "(tegra)"

RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base = ""

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
