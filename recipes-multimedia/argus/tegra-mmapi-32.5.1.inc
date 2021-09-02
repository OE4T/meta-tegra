HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

L4T_DEB_SOCNAME = "t186"
SRC_SOC_DEBS = "nvidia-l4t-jetson-multimedia-api_${PV}-20210519111140_arm64.deb;subdir=tegra-mmapi"
SRC_URI[sha256sum] = "61e24f12e1ca420fc2a31ef21000ec30979b5b9008989f1fd8f8830965db8573"

inherit l4t_deb_pkgfeed

LIC_FILES_CHKSUM = "file://LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra-mmapi/usr/src/jetson_multimedia_api"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
