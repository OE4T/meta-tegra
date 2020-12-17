FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot:${THISDIR}/files:"
SRC_URI_append_tegra = " \
    file://0001-export-distro_bootpart-in-environment-for-bootarg-ap.patch \
    file://fw_env.config \
"

require u-boot-tegra-bootimg.inc
require u-boot-tegra-extlinux.inc
