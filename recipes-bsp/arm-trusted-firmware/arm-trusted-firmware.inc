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
LIC_FILES_CHKSUM = "file://license.md;md5=5eb52e7718ec9f1c8de8525fe75a27f8"

COMPATIBLE_MACHINE = "(tegra186|tegra194)"

PACKAGECONFIG ??= "trusty"
PACKAGECONFIG[trusty] = "SPD=trusty"

B = "${WORKDIR}/build"

CFLAGS[unexport] = "1"
LDFLAGS[unexport] = "1"
AS[unexport] = "1"
LD[unexport] = "1"

TARGET_SOC = "t186"
TARGET_SOC_tegra194 = "t194"

do_configure[noexec] = "1"

EXTRA_OEMAKE = 'BUILD_BASE=${B} CROSS_COMPILE="${TARGET_PREFIX}" PLAT=tegra \
	        DEBUG=0 LOG_LEVEL=20 V=0 TARGET_SOC=${TARGET_SOC} ${PACKAGECONFIG_CONFARGS}'

do_compile() {
	oe_runmake -C ${S} all
}

do_install() {
	install -d ${D}${datadir}/trusted-os
	install -m 0644 ${B}/tegra/${TARGET_SOC}/release/bl31.bin ${D}${datadir}/trusted-os/
}

ALLOW_EMPTY_${PN} = "1"
FILES_${PN}-dev = "${datadir}/trusted-os"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
