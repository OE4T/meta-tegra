HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD-3-Clause"

SRC_COMMON_DEBS = "${@l4t_deb_pkgname(d, 'jetson-multimedia-api')};subdir=tegra-mmapi"
PV .= "${@l4t_bsp_debian_version_suffix(d, pkgname='nvidia-l4t-jetson-multimedia-api')}"

SRC_URI[sha256sum] = "ee1da863daba1dfa711c43bbf5df3d76d07b55913560ae93cfce65e746e7016a"

inherit l4t_deb_pkgfeed features_check

SRC_URI += "file://0001-Remove-DO-NOT-USE-declarations-from-v4l2_nv_extensio.patch"

REQUIRED_DISTRO_FEATURES = "opengl"

LIC_FILES_CHKSUM = "file://LICENSE.nvprop;md5=5c7c5200a29e873064f17b5bbf4d3c56 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra-mmapi/usr/src/jetson_multimedia_api"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
