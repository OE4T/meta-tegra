SOC_FAMILY = "tegra234"
L4T_BSP_NAME = "T186"
L4T_BSP_PREFIX = "Jetson"

require tegra-binaries-${PV}.inc

WORKDIR = "${TMPDIR}/work-shared/L4T-native-${SOC_FAMILY}-${PV}-${PR}"
SSTATE_SWSPEC = "sstate:tegra-binaries-native::${PV}:${PR}::${SSTATE_VERSION}:"
STAMP = "${STAMPS_DIR}/work-shared/L4T-native-${SOC_FAMILY}-${PV}-${PR}"
STAMPCLEAN = "${STAMPS_DIR}/work-shared/L4T-native-${SOC_FAMILY}-${PV}-*"

SRC_URI += "\
           file://0003-Convert-BUP_generator.py-to-Python3.patch \
           file://0004-Convert-gen_tos_part_img.py-to-Python3.patch \
           file://0005-Convert-rollback_parser.py-to-Python3.patch \
           file://0009-Remove-xxd-dependency-from-l4t_sign_image.sh.patch \
           file://0010-Rework-logging-in-l4t_sign_image.sh.patch \
           file://0013-Fix-location-of-bsp_version-file-in-l4t_bup_gen.func.patch \
           "
S = "${WORKDIR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

COMPATIBLE_MACHINE = ""

inherit native

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "tegra-helper-scripts-native"

do_compile[noexec] = "1"

BINDIR = "${bindir}/tegra234-flash"

addtask preconfigure after do_patch before do_configure

do_install() {
    install -d ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/chkbdinfo ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrabct_v2 ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegradevflash_v2 ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrahost_v2 ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegraparser_v2 ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrarcm_v2 ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrasign_v2 ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrasign_v3*py ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegraopenssl ${D}${BINDIR}
    install -d ${D}${BINDIR}/pyfdt
    install -m 0644 ${S}/bootloader/pyfdt/*.py ${D}${BINDIR}/pyfdt/
    install -m 0755 ${S}/bootloader/tegraflash*.py ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/ed25519.py ${D}${BINDIR}
    install -m 0644 ${S}/bootloader/t194.py ${D}${BINDIR}
    install -m 0644 ${S}/bootloader/t234.py ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/dtbcheck.py ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/sw_memcfg_overlay.pl ${D}${BINDIR}
    sed -i -e's,^#!/usr/bin/perl,#!/usr/bin/env perl,' ${D}${BINDIR}/sw_memcfg_overlay.pl
    install -m 0755 ${S}/bootloader/nv_smd_generator ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/BUP_generator.py ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/rollback/rollback_parser.py ${D}${BINDIR}
    install -m 0644 ${S}/bootloader/l4t_bup_gen.func ${D}${BINDIR}

    #install -m 0644 ${S}/bootloader/odmsign.func ${D}${BINDIR}

    install -m 0755 ${S}/bootloader/mkgpt ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mksparse ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mkbootimg ${D}${BINDIR}

    #install -d ${D}${BINDIR}/pkc
    #install -m 0755 ${S}/pkc/mkpkc ${D}${BINDIR}/pkc/
    #install -m 0755 ${S}/pkc/nvsecuretool ${D}${BINDIR}/pkc/
    #install -m 0755 ${S}/bootloader/tegrakeyhash ${D}${BINDIR}

    install -m 0755 ${S}/nv_tegra/tos-scripts/gen_tos_part_img.py ${D}${BINDIR}

    install -m 0755 ${S}/l4t_sign_image.sh ${D}${BINDIR}
    sed -i -e's,^\(L4T_BOOTLOADER_DIR=.*\)/bootloader,\1,' ${D}${BINDIR}/l4t_sign_image.sh
}

INHIBIT_SYSROOT_STRIP = "1"
