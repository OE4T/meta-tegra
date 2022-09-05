TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/trusty_src.tbz2"
inherit l4t_bsp
require recipes-bsp/tegra-sources/tegra-sources-${L4T_VERSION}.inc

SUMMARY:forcevariable = "Sample client application to communicate with the luks-srv TA"
DESCRIPTION:forcevariable = "Sample Client Application (CA) to communicate with the \
luks-srv Trusted Application (TA)"
LICENSE:forcevariable = "MIT"
LIC_FILES_CHKSUM:forcevariable = "file://LICENSE;md5=0f2184456a07e1ba42a53d9220768479"

S = "${WORKDIR}/trusty/app/nvidia-sample/luks-srv/CA_sample"
PV = "${L4T_VERSION}"

require recipes-bsp/trusty/trusty-l4t.inc

export CROSS_COMPILER="${STAGING_DIR_NATIVE}/gcc-linaro-baremetal-arm/bin/arm-eabi-"

do_compile() {
    oe_runmake -C ${S}
}

do_install() {
    install -d ${D}${sbindir}
    install ${S}/out/${BPN} ${D}${sbindir}/
}

FILES_${PN}-dev:remove = "${datadir}/trusted-os"
