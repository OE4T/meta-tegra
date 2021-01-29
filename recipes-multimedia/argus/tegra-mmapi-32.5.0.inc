HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

L4T_DEB_GROUP = "nvidia-l4t-jetson-multimedia-api"
SRC_SOC_DEBS = "nvidia-l4t-jetson-multimedia-api_${PV}-20210115151051_arm64.deb;subdir=tegra-mmapi"
SRC_SOC_DEBS_tegra210 = "nvidia-l4t-jetson-multimedia-api_${PV}-20210115145440_arm64.deb;subdir=tegra-mmapi"
SRC_SHA256SUM = "6b1059873492834a3b7fb89ef31d2fb393a15c39f7ff545506eee50a215853c8"
SRC_SHA256SUM_tegra210 = "81cdc4bc42a9f5c791fc5c6fc86be3bddbd2f56fb166813dad4432682d7e1779"
SRC_URI[sha256sum] = "${SRC_SHA256SUM}"

inherit l4t_deb_pkgfeed

LIC_FILES_CHKSUM = "file://LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra-mmapi/usr/src/jetson_multimedia_api"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
PACKAGE_ARCH_tegra210 = "${SOC_FAMILY_PKGARCH}"
