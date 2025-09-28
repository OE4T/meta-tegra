HOMEPAGE = "https://www.trustedfirmware.org/projects/tf-a/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://docs/license.rst;md5=b2c740efedc159745b9b31f88ff03dde"

TARGET_SOC ??= "t264"
TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/atf_${TARGET_SOC}_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-38.2.0.inc

S = "${UNPACKDIR}/arm-trusted-firmware.${TARGET_SOC}"
B = "${WORKDIR}/build"

CVE_PRODUCT = "arm:arm-trusted-firmware \
               arm:trusted_firmware-a \
               arm:arm_trusted_firmware \
               arm_trusted_firmware_project:arm_trusted_firmware"

TOOLCHAIN = "gcc"
