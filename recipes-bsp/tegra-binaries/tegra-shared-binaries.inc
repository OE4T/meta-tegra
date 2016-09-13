do_fetch() {
    :
}
do_fetch[noexec] = "1"
deltask do_unpack
deltask do_patch

SRC_URI = ""
do_configure[depends] += "tegra-binaries:do_preconfigure"
do_populate_lic[depends] += "tegra-binaries:do_unpack"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
