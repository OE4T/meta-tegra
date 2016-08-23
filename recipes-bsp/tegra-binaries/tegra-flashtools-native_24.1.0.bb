require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

inherit native

INHIBIT_DEFAULT_DEPS = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/elf-get-entry.py ${D}${bindir}
    install -m 0755 ${S}/bootloader/gen-tboot-img.py ${D}${bindir}

    install -m 0755 ${S}/bootloader/tegrabct ${D}${bindir}
    install -m 0755 ${S}/bootloader/tegradevflash ${D}${bindir}
    install -m 0755 ${S}/bootloader/tegrahost ${D}${bindir}
    install -m 0755 ${S}/bootloader/tegraparser ${D}${bindir}
    install -m 0755 ${S}/bootloader/tegrarcm ${D}${bindir}
    install -m 0755 ${S}/bootloader/tegrasign ${D}${bindir}

    install -m 0755 ${S}/bootloader/tegraflash.py ${D}${bindir}
    install -m 0644 ${S}/bootloader/tegraflash_internal.py ${D}${bindir}

    install -m 0755 ${S}/bootloader/mkgpt ${D}${bindir}
    install -m 0755 ${S}/bootloader/mksparse ${D}${bindir}
}
