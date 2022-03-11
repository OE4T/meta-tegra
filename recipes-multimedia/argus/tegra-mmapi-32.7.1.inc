HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD-3-Clause"

# This is the t186 package, but the t210 version's content is identical
L4T_DEB_SOCNAME = "t186"
L4T_BSP_DEB_VERSION = "${L4T_BSP_DEB_DEFAULT_VERSION_T186}"
SRC_SOC_DEBS = "nvidia-l4t-jetson-multimedia-api_${PV}_arm64.deb;subdir=tegra-mmapi"
PV .= "${@l4t_bsp_debian_version_suffix(d)}"

SRC_URI[sha256sum] = "8f088ce087d009b67785df56076bcd589c9fafc486c284c0660afd9b1bf8c586"

inherit l4t_deb_pkgfeed features_check

REQUIRED_DISTRO_FEATURES = "opengl"

LIC_FILES_CHKSUM = "file://LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra-mmapi/usr/src/jetson_multimedia_api"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
