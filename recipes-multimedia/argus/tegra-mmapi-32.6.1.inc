HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

SRC_SOC_DEBS = "nvidia-l4t-jetson-multimedia-api_${PV}-20210726122859_arm64.deb;subdir=tegra-mmapi"
SRC_SOC_DEBS:tegra210 = "nvidia-l4t-jetson-multimedia-api_${PV}-20210726122000_arm64.deb;subdir=tegra-mmapi"
SRC_SHA256SUM = "53c18c5e2c8296b3d047eff9f3647263cc72b7b8a6dbe3d63deb600b145640f5"
SRC_SHA256SUM:tegra210 = "93903b08c1c86b3ccf5d640e7930869656836698ef5237ecdceed916fd282bec"
SRC_URI[sha256sum] = "${SRC_SHA256SUM}"

inherit l4t_deb_pkgfeed

LIC_FILES_CHKSUM = "file://LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra-mmapi/usr/src/jetson_multimedia_api"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
PACKAGE_ARCH:tegra210 = "${SOC_FAMILY_PKGARCH}"
