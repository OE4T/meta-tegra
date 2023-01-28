HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD-3-Clause"

SRC_COMMON_DEBS = "${@l4t_deb_pkgname(d, 'jetson-multimedia-api')};subdir=tegra-mmapi"
PV .= "${@l4t_bsp_debian_version_suffix(d, pkgname='nvidia-l4t-jetson-multimedia-api')}"

SRC_URI[sha256sum] = "a436cbfdbbef87b9be8e0defb4c6af5ffa4c4ba8bd957446577a9fb30334595f"

inherit l4t_deb_pkgfeed features_check

SRC_URI += "file://0001-Remove-DO-NOT-USE-declarations-from-v4l2_nv_extensio.patch"

REQUIRED_DISTRO_FEATURES = "opengl"

LIC_FILES_CHKSUM = "file://LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra-mmapi/usr/src/jetson_multimedia_api"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
