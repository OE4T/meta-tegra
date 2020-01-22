PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/${BP}"
B = "${WORKDIR}/build"

EXTRA_OEMAKE = 'TEGRA_TOP=${S} GLOBAL_CFLAGS=${CBOOT_CFLAGS} CFLAGS= CPPFLAGS= LDFLAGS= PROJECT=t194 TOOLCHAIN_PREFIX="${TARGET_PREFIX}" DEBUG=2 \
                BUILDROOT="${B}" NV_TARGET_BOARD=t194ref NV_BUILD_SYSTEM_TYPE=l4t NOECHO=" "'

CBOOT_CFLAGS ?= ""
CBOOT_BUILD_ARTIFACT ?= "build-t194/lk.bin"
CBOOT_IMAGE ?= "cboot-${MACHINE}-${PV}-${PR}.bin"
CBOOT_SYMLINK ?= "cboot-${MACHINE}.bin"

inherit python3native deploy

def cboot_pseudo_branch(d):
    ver = d.getVar('PV').split('.')
    return '%02d.%02d' % (int(ver[0]), int(ver[1]))

do_configure() {
    sed -i -r -e's,^(BUILD_BRANCH :=).*,BUILD_BRANCH := ${@cboot_pseudo_branch(d)},' ${S}/bootloader/partner/t18x/cboot/build/version.mk
}

do_compile() {
    LIBGCC=`${CC} -print-libgcc-file-name`
    oe_runmake -C ${S}/bootloader/partner/t18x/cboot LIBGCC="$LIBGCC"
}

do_install() {
	:
}

do_deploy() {
	install -d ${DEPLOYDIR}
	install -m 0644 ${B}/${CBOOT_BUILD_ARTIFACT} ${DEPLOYDIR}/${CBOOT_IMAGE}
	ln -sf ${CBOOT_IMAGE} ${DEPLOYDIR}/${CBOOT_SYMLINK}
}

addtask deploy before do_build after do_install

python () {
    socarch = d.getVar("SOC_FAMILY") or ""
    if socarch in ['tegra186', 'tegra194'] and d.getVar("PREFERRED_PROVIDER_virtual/bootloader").startswith("cboot"):
        d.appendVar("PROVIDES", " virtual/bootloader")
}
