DESCRIPTION = "Generates a bootloader update payload for use with nv_update_engine when using U-Boot on tegra186 platforms."
LICENSE = "MIT"
HOMEPAGE = "https://github.com/OE4T/meta-tegra"

COMPATIBLE_MACHINE = "(tegra)"

INHIBIT_DEFAULT_DEPS = "1"

inherit nopackages image_types_tegra deploy

deltask do_fetch
deltask do_unpack
deltask do_patch
deltask do_configure
deltask do_compile
deltask do_install
deltask do_populate_sysroot

UBOOT_SUFFIX ??= "bin"
UBOOT_BINARY ?= "u-boot-dtb.${UBOOT_SUFFIX}"
UBOOT_IMAGE ?= "u-boot-${MACHINE}.${UBOOT_SUFFIX}"

do_deploy() {
    if [ -n "${UBOOT_CONFIG}" ]
    then
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]
                then
		    oe_make_bup_payload ${DEPLOY_DIR_IMAGE}/u-boot-${type}.${UBOOT_SUFFIX}
                    install -d ${DEPLOYDIR}
                    install -m 644 ${WORKDIR}/bup-payload/bl_update_payload ${DEPLOYDIR}/u-boot-${type}.${UBOOT_SUFFIX}.bup-payload
		    for f in ${WORKDIR}/bup-payload/*_only_payload; do
			[ -e $f ] || continue
			sfx=$(basename $f _payload)
			install -m 0644 $f ${DEPLOYDIR}/u-boot-${type}.${UBOOT_SUFFIX}.$sfx.bup-payload
			ln -sf u-boot-${type}.${UBOOT_SUFFIX}.$sfx.bup-payload ${DEPLOYDIR}/${UBOOT_IMAGE}-${type}.$sfx.bup-payload
			ln -sf u-boot-${type}.${UBOOT_SUFFIX}.$sfx.bup-payload ${DEPLOYDIR}/${UBOOT_IMAGE}.$sfx.bup-payload
			ln -sf u-boot-${type}.${UBOOT_SUFFIX}.$sfx.bup-payload ${DEPLOYDIR}/${UBOOT_BINARY}-${type}.$sfx.bup-payload
			ln -sf u-boot-${type}.${UBOOT_SUFFIX}.$sfx.bup-payload ${DEPLOYDIR}/${UBOOT_BINARY}.$sfx.bup-payload
		    done
                    cd ${DEPLOYDIR}
                    ln -sf u-boot-${type}.${UBOOT_SUFFIX}.bup-payload ${UBOOT_IMAGE}-${type}.bup-payload
                    ln -sf u-boot-${type}.${UBOOT_SUFFIX}.bup-payload ${UBOOT_IMAGE}.bup-payload
                    ln -sf u-boot-${type}.${UBOOT_SUFFIX}.bup-payload ${UBOOT_BINARY}-${type}.bup-payload
                    ln -sf u-boot-${type}.${UBOOT_SUFFIX}.bup-payload ${UBOOT_BINARY}.bup-payload
                fi
            done
            unset  j
        done
        unset  i
    else
	oe_make_bup_payload ${DEPLOY_DIR_IMAGE}/${UBOOT_IMAGE}
        install -d ${DEPLOYDIR}
        rm -f ${DEPLOYDIR}/${UBOOT_BINARY}.bup-payload
        install -m 644 ${WORKDIR}/bup-payload/bl_update_payload ${DEPLOYDIR}/${UBOOT_IMAGE}.bup-payload
        ln -sf ${UBOOT_IMAGE}.bup-payload ${DEPLOYDIR}/${UBOOT_BINARY}.bup-payload
	for f in ${WORKDIR}/bup-payload/*_only_payload; do
	    [ -e $f ] || continue
	    sfx=$(basename $f _payload)
	    install -m 0644 $f ${DEPLOYDIR}/${UBOOT_IMAGE}.$sfx.bup-payload
	    ln -sf ${UBOOT_IMAGE}.$sfx.bup-payload ${DEPLOYDIR}/${UBOOT_BINARY}.$sfx.bup-payload
	done
    fi
}
do_deploy[depends] += "virtual/bootloader:do_deploy virtual/kernel:do_deploy ${SOC_FAMILY}-flashtools-native:do_populate_sysroot"
do_deploy[depends] += "tegra-redundant-boot-base:do_populate_sysroot tegra-bootfiles:do_populate_sysroot"
do_deploy[depends] += "coreutils-native:do_populate_sysroot cboot:do_deploy virtual/secure-os:do_deploy"
addtask deploy before do_build

PACKAGE_ARCH = "${MACHINE_ARCH}"
