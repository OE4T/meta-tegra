HOMEPAGE = "https://www.trustedfirmware.org/projects/tf-a/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://docs/license.rst;md5=b2c740efedc159745b9b31f88ff03dde"

TARGET_SOC ??= "t264"
TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/atf_${TARGET_SOC}_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-39.2.2.inc

S = "${UNPACKDIR}/arm-trusted-firmware.${TARGET_SOC}"
B = "${WORKDIR}/build"

CVE_PRODUCT = "trustedfirmware:trusted_firmware-a \
               arm:trusted_firmware-a \
               arm:arm-trusted-firmware \
               arm:arm_trusted_firmware"
CVE_VERSION = "${@d.getVar('PV').split('-l4t-')[0]}"

CVE_STATUS[CVE-2022-47630] = "fixed-version: fix was included in v2.8.1, not reflected in NVD record"
CVE_STATUS[CVE-2023-31339] = "not-applicable-platform: applies to AMD Zynq UltraScale+ MPSoC/RFSoC platform code, not Tegra"

TOOLCHAIN = "gcc"
