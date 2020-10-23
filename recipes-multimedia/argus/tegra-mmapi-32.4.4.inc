HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

L4T_DEB_GROUP = "nvidia-l4t-jetson-multimedia-api"
SRC_SOC_DEBS = "nvidia-l4t-jetson-multimedia-api_${PV}-20201016123640_arm64.deb;subdir=tegra-mmapi"
SRC_SOC_DEBS_tegra210 = "nvidia-l4t-jetson-multimedia-api_${PV}-20201016124427_arm64.deb;subdir=tegra-mmapi"
SRC_SHA256SUM = "3dc8d1586a04daf79d0637ec0aff79806048e39eb342957017330a011c706da9"
SRC_SHA256SUM_tegra210 = "d5f519f53e7b94a26e7b166057c3dd87232e5ea65caac825fed2c07a5533be65"
SRC_URI[sha256sum] = "${SRC_SHA256SUM}"

inherit l4t_deb_pkgfeed

LIC_FILES_CHKSUM = "file://LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra-mmapi/usr/src/jetson_multimedia_api"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
PACKAGE_ARCH_tegra210 = "${SOC_FAMILY_PKGARCH}"
