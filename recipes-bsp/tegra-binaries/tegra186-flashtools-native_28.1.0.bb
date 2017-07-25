require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = ""

inherit native

INHIBIT_DEFAULT_DEPS = "1"
do_compile[noexec] = "1"

BINDIR = "${bindir}/tegra186-flash"

do_install() {
    install -d ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/chkbdinfo ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrabct_v2 ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegradevflash_v2 ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrahost_v2 ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegraparser_v2 ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrarcm_v2 ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrasign_v2 ${D}${BINDIR}

    install -m 0755 ${S}/bootloader/tegraflash.py ${D}${BINDIR}
    install -m 0644 ${S}/bootloader/tegraflash_internal.py ${D}${BINDIR}

    install -m 0755 ${S}/bootloader/mkgpt ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mksparse ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mkbootimg ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mkbctpart ${D}${BINDIR}
    install -m 0755 ${S}/tegra186-flash-helper.sh ${D}${BINDIR}
}
