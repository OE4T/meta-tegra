HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "LicenseRef-Proprietary AND BSD-3-Clause"

SRC_COMMON_DEBS = "${@l4t_deb_pkgname(d, 'jetson-multimedia-api')};subdir=tegra-mmapi"
PV .= "${@l4t_bsp_debian_version_suffix(d, pkgname='nvidia-l4t-jetson-multimedia-api')}"

SRC_URI[sha256sum] = "e8e6b2d0541cd0156d5e677a9bfb2173af9c3a2510327fb0a9c7539a83c4216d"

inherit l4t_deb_pkgfeed features_check

SRC_URI += "file://0001-Remove-DO-NOT-USE-declarations-from-v4l2_nv_extensio.patch"

REQUIRED_DISTRO_FEATURES = "opengl"

LIC_FILES_CHKSUM = "file://LICENSE.nvprop;md5=5c7c5200a29e873064f17b5bbf4d3c56 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${UNPACKDIR}/tegra-mmapi/usr/src/jetson_multimedia_api"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
