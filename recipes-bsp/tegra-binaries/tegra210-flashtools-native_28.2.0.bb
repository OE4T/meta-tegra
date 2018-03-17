require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = ""

inherit native

INHIBIT_DEFAULT_DEPS = "1"
do_compile[noexec] = "1"

BINDIR = "${bindir}/tegra210-flash"

do_install() {
    install -d ${D}${BINDIR}
    install -m 0755 ${S}/elf-get-entry.py ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/gen-tboot-img.py ${D}${BINDIR}

    install -m 0755 ${S}/bootloader/tegrabct ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegradevflash ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrahost ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegraparser ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrarcm ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrasign ${D}${BINDIR}

    install -m 0755 ${S}/bootloader/tegraflash.py ${D}${BINDIR}
    install -m 0644 ${S}/bootloader/tegraflash_internal.py ${D}${BINDIR}

    install -m 0755 ${S}/bootloader/mkgpt ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mksparse ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mkbootimg ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mkbctpart ${D}${BINDIR}
}
