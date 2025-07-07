SUMMARY = "Trusted Firmware-A - L4T distribution"
DESCRIPTION = "Trusted Firmware-A (TF-A) provides a reference implementation of secure world software \
for Armv7-A and Armv8-A, including a Secure Monitor executing at Exception Level 3 (EL3)."
HOMEPAGE = "https://www.trustedfirmware.org/projects/tf-a/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://docs/license.rst;md5=b2c740efedc159745b9b31f88ff03dde"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/atf_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-35.6.2.inc

SRC_URI += "file://0001-workaround-to-fix-ld.bfd-warning-binutils-version-2..patch"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS:append = " virtual/${TARGET_PREFIX}gcc"

S = "${WORKDIR}/arm-trusted-firmware"
B = "${WORKDIR}/build"

COMPATIBLE_MACHINE = "(tegra194|tegra234)"

CVE_PRODUCT = "arm:arm-trusted-firmware \
               arm:trusted_firmware-a \
               arm:arm_trusted_firmware \
               arm_trusted_firmware_project:arm_trusted_firmware"

PACKAGECONFIG ??= "optee"
PACKAGECONFIG[trusty] = "SPD=trusty"
PACKAGECONFIG[optee] = "SPD=opteed"

CFLAGS[unexport] = "1"
LDFLAGS[unexport] = "1"
AS[unexport] = "1"
LD[unexport] = "1"

TARGET_SOC = "UNKNOWN"
TARGET_SOC:tegra194 = "t194"
TARGET_SOC:tegra234 = "t234"

def generate_build_string(d):
    pv = d.getVar('PV').split('-')
    if len(pv) > 1:
        return 'BUILD_STRING={}'.format('-'.join(pv[1:]))

def generate_build_timestamp(d):
    from datetime import datetime
    sde = d.getVar('SOURCE_DATE_EPOCH')
    if sde:
        return 'BUILD_MESSAGE_TIMESTAMP="\\\"{}\\\""'.format(datetime.utcfromtimestamp(int(sde)).strftime('%Y-%m-%d %H:%M:%S'))
    return ''

BUILD_STRING ?= "${@generate_build_string(d)}"
BUILDTIMESTAMP ?= "${@generate_build_timestamp(d)}"

ATF_DEBUG ?= "0"
ATF_LOG_LEVEL ?= "20"
EXTRA_OEMAKE = 'BUILD_BASE=${B} CROSS_COMPILE="${TARGET_PREFIX}" PLAT=tegra \
	        DEBUG=${ATF_DEBUG} LOG_LEVEL=${ATF_LOG_LEVEL} V=1 TARGET_SOC=${TARGET_SOC} \
	        ${BUILDTIMESTAMP} ${BUILD_STRING} ${PACKAGECONFIG_CONFARGS}'

do_configure[noexec] = "1"

do_compile() {
	oe_runmake -C ${S} all
}
do_compile[cleandirs] = "${B}"

do_install() {
	install -d ${D}${datadir}/trusted-os
	install -m 0644 ${B}/tegra/${TARGET_SOC}/release/bl31.bin ${D}${datadir}/trusted-os/
}

ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-dev = "${datadir}/trusted-os"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
