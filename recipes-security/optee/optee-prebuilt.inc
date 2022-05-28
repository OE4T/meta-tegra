HOMEPAGE = "https://github.com/OP-TEE"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/nvidia-l4t-optee/copyright;md5=38ef63b8f3232378d9f652f640ee0a3f"

SRC_SOC_DEBS = "nvidia-l4t-optee_${PV}_arm64.deb;subdir=optee"
PV .= "${@l4t_bsp_debian_version_suffix(d)}"

inherit l4t_deb_pkgfeed

SRC_URI[sha256sum] = "df3db9095f992ee35f76c9a3c94e19c23b41ac066bade476d94c5357da3ebefd"

COMPATIBLE_MACHINE = "(tegra)"

S = "${WORKDIR}/optee"

do_configure() {
    :
}

do_compile() {
    :
}
