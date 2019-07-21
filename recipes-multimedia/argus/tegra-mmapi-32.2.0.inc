HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

SRC_URI = "${L4T_URI_BASE}/Tegra_Multimedia_API_R${PV}_aarch64.tbz2;downloadfilename=Tegra_Multimedia_API_R${PV}-${L4T_BSP_NAME}.tbz2"
SRC_MD5SUM = "09c929b296af2653f72c5fcd0cd013b8"
SRC_SHA256SUM = "d3e8059fda89def72882ae3a0cfe69c1771e179adbd1d9d69c660b6855b48c02"
SRC_MD5SUM_tegra210 = "2950b7256ee31a8b14e083c44ba7c111"
SRC_SHA256SUM_tegra210 = "e847fc7b9befaf5300452708247c81207c1eb31161d4ca8063256e32b3359c80"
SRC_URI[md5sum] = "${SRC_MD5SUM}"
SRC_URI[sha256sum] = "${SRC_SHA256SUM}"

inherit l4t_bsp

LIC_FILES_CHKSUM = "file://LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra_multimedia_api"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"
COMPATIBLE_MACHINE_tegra124 = "(-)"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

