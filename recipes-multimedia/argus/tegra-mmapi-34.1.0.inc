HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD-3-Clause"

L4T_DEB_SOCNAME = "t194"
SRC_SOC_DEBS = "nvidia-l4t-jetson-multimedia-api_${PV}_arm64.deb;subdir=tegra-mmapi"
PV .= "${@l4t_bsp_debian_version_suffix(d)}"

<<<<<<<< HEAD:recipes-multimedia/argus/tegra-mmapi-32.7.2.inc
SRC_URI[sha256sum] = "40225cf5279fc8ded8a29966e7923d1bdf5a7eb9ff5bee2c016fec2db40a786a"
========
SRC_URI[sha256sum] = "a1d266d7b21f2cb6a8ce0cf922d56495f7881a3639fd2adcc9afc517c22bd838"
>>>>>>>> e23743ce (tegra-mmapi: update for R34.1.0):recipes-multimedia/argus/tegra-mmapi-34.1.0.inc

inherit l4t_deb_pkgfeed features_check

SRC_URI += "file://0005-Remove-DO-NOT-USE-declarations-from-v4l2_nv_extensio.patch"

REQUIRED_DISTRO_FEATURES = "opengl"

LIC_FILES_CHKSUM = "file://LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra-mmapi/usr/src/jetson_multimedia_api"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
