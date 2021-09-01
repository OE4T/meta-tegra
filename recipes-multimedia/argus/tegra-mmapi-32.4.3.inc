HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

SRC_SOC_DEBS = "nvidia-l4t-jetson-multimedia-api_${PV}-20200625213407_arm64.deb;subdir=tegra-mmapi"
SRC_URI[sha256sum] = "a639090aa97441790f59e6946cdd558e8d2873678421b4cf6fdee77adfb22d1d"
FILESEXTRAPATHS_prepend := "${THISDIR}/tegra-mmapi:"

inherit l4t_deb_pkgfeed

SRC_URI += "file://0001-Add-getMinAeRegionSize-method.patch"

LIC_FILES_CHKSUM = "file://LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra-mmapi/usr/src/jetson_multimedia_api"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
