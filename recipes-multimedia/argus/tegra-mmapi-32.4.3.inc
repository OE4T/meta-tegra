HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

L4T_DEB_GROUP = "nvidia-l4t-jetson-multimedia-api"
SRC_SOC_DEBS = "nvidia-l4t-jetson-multimedia-api_${PV}-20200625213407_arm64.deb;subdir=tegra-mmapi"
SRC_SOC_DEBS_tegra210 = "nvidia-l4t-jetson-multimedia-api_${PV}-20200625213809_arm64.deb;subdir=tegra-mmapi"
SRC_SHA256SUM = "a639090aa97441790f59e6946cdd558e8d2873678421b4cf6fdee77adfb22d1d"
SRC_SHA256SUM_tegra210 = "d5ae20bd5d220cbe2af3e54d3047c18b558c6e59a46ec06215dc08bf4e87609f"
SRC_URI[sha256sum] = "${SRC_SHA256SUM}"
FILESEXTRAPATHS_prepend := "${THISDIR}/tegra-mmapi:"

inherit l4t_deb_pkgfeed

SRC_URI += "file://0001-Add-getMinAeRegionSize-method.patch"

LIC_FILES_CHKSUM = "file://LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra-mmapi/usr/src/jetson_multimedia_api"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
PACKAGE_ARCH_tegra210 = "${SOC_FAMILY_PKGARCH}"
