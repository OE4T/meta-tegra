HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

SRC_DEBPKG = "${NVIDIA_DEVNET_MIRROR}/nvidia-l4t-jetson-multimedia-api_${PV}-20191209230245_arm64.deb;subdir=tegra-mmapi"
SRC_DEBPKG_tegra210 = "${NVIDIA_DEVNET_MIRROR}/nvidia-l4t-jetson-multimedia-api_${PV}-20191209225816_arm64.deb;subdir=tegra-mmapi"
SRC_URI = "${SRC_DEBPKG}"
SRC_MD5SUM = "47b819b7d0a1777ac77856d98c42f66e"
SRC_SHA256SUM = "9e8217d0fe7247bf862364f66a9acaac8e3b31206be5db4981c98ae1c141c3a1"
SRC_MD5SUM_tegra210 = "4fd3d8c1579bc998fd1b8e52d31532ba"
SRC_SHA256SUM_tegra210 = "3d65cbc153052167b722f3eadecfc6aef25899de8663bb52d9d3c957318d2fe7"
SRC_URI[md5sum] = "${SRC_MD5SUM}"
SRC_URI[sha256sum] = "${SRC_SHA256SUM}"

inherit nvidia_devnet_downloads

LIC_FILES_CHKSUM = "file://LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra-mmapi/usr/src/jetson_multimedia_api"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

