HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

SRC_SOC_DEBS = "nvidia-l4t-jetson-multimedia-api_${PV}-20210519111140_arm64.deb;subdir=tegra-mmapi"
SRC_SOC_DEBS:tegra210 = "nvidia-l4t-jetson-multimedia-api_${PV}-20210519110732_arm64.deb;subdir=tegra-mmapi"
SRC_SHA256SUM = "61e24f12e1ca420fc2a31ef21000ec30979b5b9008989f1fd8f8830965db8573"
SRC_SHA256SUM:tegra210 = "0678aa3f5f8682e19b5aa8e8a8b0fa203ad72f3813b54e2cfe8bb179f3a213e5"
SRC_URI[sha256sum] = "${SRC_SHA256SUM}"

inherit l4t_deb_pkgfeed

LIC_FILES_CHKSUM = "file://LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra-mmapi/usr/src/jetson_multimedia_api"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
PACKAGE_ARCH:tegra210 = "${SOC_FAMILY_PKGARCH}"
