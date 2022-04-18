SUMMARY = "ARM Trusted Firmware - L4T distribution"
DESCRIPTION = "ARM Trusted Firmware provides a reference implementation of secure world \
software for ARMv8-A, including a Secure Monitor executing at \
Exception Level 3 (EL3). It implements various ARM interface standards, such as \
the Power State Coordination Interface (PSCI), Trusted Board Boot Requirements \
(TBBR, ARM DEN0006C-1) and SMC Calling Convention (SMCCC). As far as possible \
the code is designed for reuse or porting to other ARMv8-A model and hardware \
platforms."
HOMEPAGE = "https://www.github.com/ARM-software/arm-trusted-firmware"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://docs/license.rst;md5=b2c740efedc159745b9b31f88ff03dde"

COMPATIBLE_MACHINE = "(tegra186|tegra194|tegra234)"

PACKAGECONFIG ??= "optee"
PACKAGECONFIG[trusty] = "SPD=trusty"
PACKAGECONFIG[optee] = "SPD=opteed"

B = "${WORKDIR}/build"

CFLAGS[unexport] = "1"
LDFLAGS[unexport] = "1"
AS[unexport] = "1"
LD[unexport] = "1"

TARGET_SOC = "UNKNOWN"
TARGET_SOC:tegra194 = "t194"
TARGET_SOC:tegra234 = "t234"

def generate_build_timestamp(d):
    from datetime import datetime
    sde = d.getVar('SOURCE_DATE_EPOCH')
    if sde:
        return 'BUILD_MESSAGE_TIMESTAMP="\\\"{}\\\""'.format(datetime.utcfromtimestamp(int(sde)).strftime('%Y-%m-%d %H:%M:%S'))
    return ''

BUILDTIMESTAMP ?= "${@generate_build_timestamp(d)}"
BUILD_STRING ??= ""

EXTRA_OEMAKE = 'BUILD_BASE=${B} CROSS_COMPILE="${TARGET_PREFIX}" PLAT=tegra \
	        DEBUG=0 LOG_LEVEL=20 V=0 TARGET_SOC=${TARGET_SOC} ${BUILDTIMESTAMP} ${BUILD_STRING} ${PACKAGECONFIG_CONFARGS}'

do_configure:append() {
	oe_runmake -C ${S} clean
}

do_compile() {
	export CFLAGS="-Wno-error=array-bounds"
	oe_runmake -C ${S} all
}

do_install() {
	install -d ${D}${datadir}/trusted-os
	install -m 0644 ${B}/tegra/${TARGET_SOC}/release/bl31.bin ${D}${datadir}/trusted-os/
}

ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-dev = "${datadir}/trusted-os"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
