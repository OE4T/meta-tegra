DESCRIPTION = "NVIDIA CUDA utils"
L4T_DEB_COPYRIGHT_MD5 = "1df45365b467c00fc5fb22df229af6e6"
DEPENDS = "tegra-libraries-cuda"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-cuda-utils"

require tegra-debian-libraries-common.inc

MAINSUM = "14f3cc0044964fe10adf6ffe7b0ab1bfb9bc696bd7ea3cb1c3faebc80657d2a3"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/usr/bin/nvidia-cuda-mps-* ${D}${bindir}/
}

PACKAGES = "${PN}"
FILES:${PN} = "${bindir}"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
