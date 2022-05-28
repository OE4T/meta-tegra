HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD-3-Clause"

L4T_DEB_SOCNAME = "t194"
SRC_SOC_DEBS = "nvidia-l4t-jetson-multimedia-api_${PV}_arm64.deb;subdir=tegra-mmapi"
PV .= "${@l4t_bsp_debian_version_suffix(d)}"

SRC_URI[sha256sum] = "7e6c621dcfebda812c01513b5d0bb5dae3633d355082dae8309bb422d4f7331b"

inherit l4t_deb_pkgfeed features_check

SRC_URI += "file://0005-Remove-DO-NOT-USE-declarations-from-v4l2_nv_extensio.patch"

REQUIRED_DISTRO_FEATURES = "opengl"

LIC_FILES_CHKSUM = "file://LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra-mmapi/usr/src/jetson_multimedia_api"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
