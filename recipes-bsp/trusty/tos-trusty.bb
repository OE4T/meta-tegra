DESCRIPTION = "Construct a trusted OS image with ATF and Trusty"
LICENSE = "MIT"
HOMEPAGE = "https://github.com/OE4T/meta-tegra"

COMPATIBLE_MACHINE = "(tegra186|tegra194)"

DEPENDS = "tegra186-flashtools-native openssl-native arm-trusted-firmware trusty-l4t"

inherit l4t_bsp deploy nopackages pythonnative

PV = "${L4T_VERSION}"

PROVIDES += "virtual/secure-os"

TOSIMG = "tos-trusty.img"
TOSIMG_tegra194 = "tos-trusty_t194.img"

TOS_IMAGE ?= "tos-${MACHINE}-${PV}-${PR}.img"
TOS_SYMLINK ?= "tos-${MACHINE}.img"

S = "${WORKDIR}/${BP}"

do_compile() {
    python ${STAGING_BINDIR_NATIVE}/tegra186-flash/gen_tos_part_img.py \
        --monitor ${STAGING_DATADIR}/trusted-os/bl31.bin \
        --os ${STAGING_DATADIR}/trusted-os/lk.bin \
        ${TOSIMG}
}

do_install[noexec] = "1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${B}/${TOSIMG} ${DEPLOYDIR}/${TOS_IMAGE}
    ln -sf ${TOS_IMAGE} ${DEPLOYDIR}/${TOS_SYMLINK}
}

addtask deploy before do_build after do_compile
