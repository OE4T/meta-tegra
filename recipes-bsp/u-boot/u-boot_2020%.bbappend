FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot:${THISDIR}/files:"
SRC_URI += "file://0001-export-distro_bootpart-in-environment-for-bootarg-ap.patch"
SRC_URI_append_tegra = " file://fw_env.config"

require u-boot-tegra-bootimg.inc
require u-boot-tegra-extlinux.inc
