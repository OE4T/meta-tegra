HOMEPAGE = "https://github.com/OP-TEE"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/nvidia-l4t-optee/copyright;md5=38ef63b8f3232378d9f652f640ee0a3f"

SRC_SOC_DEBS = "${@l4t_deb_pkgname(d, 'optee')};subdir=optee"
PV .= "${@l4t_bsp_debian_version_suffix(d, pkgname='nvidia-l4t-optee')}"

inherit l4t_deb_pkgfeed

SRC_URI[sha256sum] = "e2468b00cae45db096aef67173ef86ddca421ee5db8a54d6096eccdc047a8ed3"

COMPATIBLE_MACHINE = "(tegra)"

S = "${WORKDIR}/optee"

do_configure() {
    :
}

do_compile() {
    :
}
