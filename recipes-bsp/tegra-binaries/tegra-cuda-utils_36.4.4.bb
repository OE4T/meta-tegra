DESCRIPTION = "NVIDIA CUDA utils"
L4T_DEB_COPYRIGHT_MD5 = "3eee7940f3706b5eb2411a575788e804"
DEPENDS = "tegra-libraries-cuda"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-cuda-utils"

require tegra-debian-libraries-common.inc

MAINSUM = "be4e1095d4a8e8b9cf9c7c511ba53be1acd77677f95234f30331957e082dee82"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/usr/bin/nvidia-cuda-mps-* ${D}${bindir}/
}

PACKAGES = "${PN}"
FILES:${PN} = "${bindir}"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
