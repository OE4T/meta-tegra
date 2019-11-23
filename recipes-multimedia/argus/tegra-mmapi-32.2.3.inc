HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

SRC_URI = "${L4T_URI_BASE}/Tegra_Multimedia_API_R${PV}_aarch64.tbz2;downloadfilename=Tegra_Multimedia_API_R${PV}-${L4T_BSP_NAME}.tbz2"
SRC_MD5SUM = "71545e95c328aedc9c407e850bb61585"
SRC_SHA256SUM = "aa32e46d5298278014ab76e09125ff263a3f241b42abbe9b257b79f527bcc4e0"
SRC_MD5SUM_tegra210 = "482f18709c6843cb121d80479ae59736"
SRC_SHA256SUM_tegra210 = "e9035e77c1ea1ec66489b68a17c8967497c326baef230af0e8dee6954305e8c9"
SRC_URI[md5sum] = "${SRC_MD5SUM}"
SRC_URI[sha256sum] = "${SRC_SHA256SUM}"

inherit l4t_bsp

LIC_FILES_CHKSUM = "file://LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

S = "${WORKDIR}/tegra_multimedia_api"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

