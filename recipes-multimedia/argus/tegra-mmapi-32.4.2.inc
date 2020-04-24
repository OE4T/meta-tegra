HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

L4T_DEB_GROUP = "nvidia-l4t-jetson-multimedia-api"
SRC_SOC_DEBS = "nvidia-l4t-jetson-multimedia-api_${PV}-20200408182620_arm64.deb;subdir=tegra-mmapi"
SRC_SOC_DEBS_tegra210 = "nvidia-l4t-jetson-multimedia-api_${PV}-20200408182156_arm64.deb;subdir=tegra-mmapi"
SRC_SHA256SUM = "b9f85647eda45afca4883d28b0d36fedceb2dc8c978bb1d07db0f371a39783e1"
SRC_SHA256SUM_tegra210 = "ea918f039c66b5c86980090e1ac7dbb8b9bc66efb138620cb3e451076bc03494"
SRC_URI[sha256sum] = "${SRC_SHA256SUM}"

inherit l4t_deb_pkgfeed

LIC_FILES_CHKSUM = "file://LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra-mmapi/usr/src/jetson_multimedia_api"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

