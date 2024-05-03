# Install EDK2 Base Tools in native sysroot. Currently the BaseTools are not
# built, they are just copied to native sysroot. This is sufficient for
# generating UEFI capsules as it only depends on some python scripts. Other
# tools need to be built first before adding to sysroot.

SUMMARY = "EDK2 Base Tools"
LICENSE = "BSD-2-Clause-Patent"

require edk2-firmware-core-tegra-36.3.0.inc

inherit native

RDEPENDS:${PN} += "python3-core"

do_install () {
    mkdir -p ${D}${bindir}/edk2-BaseTools
    cp -r ${S}/BaseTools/* ${D}${bindir}/edk2-BaseTools/
}
