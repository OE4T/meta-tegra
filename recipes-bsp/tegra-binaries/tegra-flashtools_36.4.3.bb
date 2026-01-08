LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://Tegra_Software_License_Agreement-Tegra-Linux.txt;md5=2d48d198004cfe9c3bc0df31c1b89805 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "\
    ${L4T_URI_BASE}/${L4T_BSP_PREFIX}_Linux_R${L4T_VERSION}_aarch64.tbz2 \
"

SRC_URI[sha256sum] = "949a44049c4ce6a8efdf572ea0820c874f6ee5d41ca3e4935b9f0e38d11873d2"

inherit l4t_bsp

SRC_URI += "\
           file://0003-Convert-BUP_generator.py-to-Python3.patch \
           file://0004-Convert-gen_tos_part_img.py-to-Python3.patch \
           file://0006-Update-tegra-python-scripts-for-Python3.patch \
           file://0009-Remove-xxd-dependency-from-l4t_sign_image.sh.patch \
           file://0010-Rework-logging-in-l4t_sign_image.sh.patch \
           file://0013-Fix-location-of-bsp_version-file-in-l4t_bup_gen.func.patch \
           "
S = "${WORKDIR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

COMPATIBLE_HOST = "(x86_64.*)"
COMPATIBLE_HOST:class-native = ""

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "tegra-helper-scripts python3-pyyaml"

do_compile[noexec] = "1"

BINDIR = "${bindir}/tegra-flash"

do_install() {
    install -d ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/chkbdinfo ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrabct_v2 ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegradevflash_v2 ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrahost_v2 ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegraparser_v2 ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrarcm_v2 ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrasign_v2 ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrasign_v3* ${D}${BINDIR}
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
    install -m 0755 ${S}/bootloader/BUP_generator.py ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/rollback/rollback_parser.py ${D}${BINDIR}
    sed -i -e's,^#!.*,#!/usr/bin/env python3,' ${D}${BINDIR}/rollback_parser.py
    install -m 0644 ${S}/bootloader/l4t_bup_gen.func ${D}${BINDIR}

    install -m 0644 ${S}/bootloader/odmsign.func ${D}${BINDIR}

    install -m 0755 ${S}/bootloader/mksparse ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mkbootimg ${D}${BINDIR}

    install -m 0755 ${S}/nv_tegra/tos-scripts/gen_tos_part_img.py ${D}${BINDIR}

    install -m 0755 ${S}/l4t_sign_image.sh ${D}${BINDIR}
    sed -i -e's,^\(L4T_BOOTLOADER_DIR=.*\)/bootloader,\1,' ${D}${BINDIR}/l4t_sign_image.sh
}

RDEPENDS:${PN} = "tegra-helper-scripts python3-pyyaml bash"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
# Some of the prebuilt tools are for 32-bit x86 instead of x86-64
INSANE_SKIP:${PN} = "arch"

BBCLASSEXTEND = "native nativesdk"
