DESCRIPTION = "NVIDIA CUDA utils"
L4T_DEB_COPYRIGHT_MD5 = "1df45365b467c00fc5fb22df229af6e6"
DEPENDS = "tegra-libraries-cuda"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-cuda-utils"

require tegra-debian-libraries-common.inc

MAINSUM = "817ce42e9d6e22199e680d0046f7bab398c3b745dd4dbf9140d4ee125825a3cb"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/usr/bin/nvidia-cuda-mps-* ${D}${bindir}/
}

PACKAGES = "${PN}"
FILES:${PN} = "${bindir}"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
