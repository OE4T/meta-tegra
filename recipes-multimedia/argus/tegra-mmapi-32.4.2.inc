HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

SRC_DEBPKG = "https://repo.download.nvidia.com/jetson/t186/pool/main/n/nvidia-l4t-jetson-multimedia-api/nvidia-l4t-jetson-multimedia-api_${PV}-20200408182620_arm64.deb;subdir=tegra-mmapi"
SRC_DEBPKG_tegra210 = "https://repo.download.nvidia.com/jetson/t210/pool/main/n/nvidia-l4t-jetson-multimedia-api/nvidia-l4t-jetson-multimedia-api_${PV}-20200408182156_arm64.deb;subdir=tegra-mmapi"
SRC_URI = "${SRC_DEBPKG}"
SRC_SHA256SUM = "b9f85647eda45afca4883d28b0d36fedceb2dc8c978bb1d07db0f371a39783e1"
SRC_SHA256SUM_tegra210 = "ea918f039c66b5c86980090e1ac7dbb8b9bc66efb138620cb3e451076bc03494"
SRC_URI[sha256sum] = "${SRC_SHA256SUM}"

inherit nvidia_devnet_downloads

LIC_FILES_CHKSUM = "file://LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra-mmapi/usr/src/jetson_multimedia_api"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
PACKAGE_ARCH_tegra210 = "${SOC_FAMILY_PKGARCH}"
