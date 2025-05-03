DESCRIPTION = "NVIDIA CUDA utils"
L4T_DEB_COPYRIGHT_MD5 = "3eee7940f3706b5eb2411a575788e804"
DEPENDS = "tegra-libraries-cuda"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-cuda-utils"

require tegra-debian-libraries-common.inc

MAINSUM = "b57b29bbdfd13611c3c859411d422d72ed044efa6a92145e72e720e43d7072cd"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/usr/bin/nvidia-cuda-mps-* ${D}${bindir}/
}

PACKAGES = "${PN}"
FILES:${PN} = "${bindir}"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
