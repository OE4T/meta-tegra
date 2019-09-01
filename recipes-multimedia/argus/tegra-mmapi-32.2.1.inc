HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

SRC_URI = "${L4T_URI_BASE}/Tegra_Multimedia_API_R${PV}_aarch64.tbz2;downloadfilename=Tegra_Multimedia_API_R${PV}-${L4T_BSP_NAME}.tbz2"
SRC_MD5SUM = "c92e0425881203ac0e65c299daff1868"
SRC_SHA256SUM = "ea90838ef6ae93bf24f07f25a02cc78b3e59bd7d3813f6312717e4d34ace78e3"
SRC_MD5SUM_tegra210 = "29fa9182ad21f8923a8cf4ba08c5a096"
SRC_SHA256SUM_tegra210 = "5b426f1663a1a3729c17a24e391980c4ee103d545384926d2148aaf22cccb574"
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

