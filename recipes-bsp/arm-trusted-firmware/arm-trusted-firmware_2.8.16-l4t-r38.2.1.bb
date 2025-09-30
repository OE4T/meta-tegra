SUMMARY = "Trusted Firmware-A - L4T distribution for Tegra264"
DESCRIPTION = "Trusted Firmware-A (TF-A) provides a reference implementation of secure world software \
for Armv7-A and Armv8-A, including a Secure Monitor executing at Exception Level 3 (EL3)."

require arm-trusted-firmware-2.8.16-l4t-r38.2.1.inc

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS:append = " virtual/cross-cc dtc-native l4t-atf-tools-native"

COMPATIBLE_MACHINE = "(tegra264)"

TOOLCHAIN = "gcc"

CFLAGS[unexport] = "1"
LDFLAGS[unexport] = "1"
AS[unexport] = "1"
LD[unexport] = "1"

TARGET_SOC = "UNKNOWN"
TARGET_SOC:tegra264 = "t264"

TARGET_SOC_OEMAKE = ""
TARGET_SOC_OEMAKE:tegra264 = "ARM_ARCH_MINOR=6 SPD=spmd SP_LAYOUT_FILE=${S}/secure_partition/sp_layout.json CTX_INCLUDE_EL2_REGS=1"

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
EXTRA_OEMAKE = 'BUILD_BASE=${B} CROSS_COMPILE="${TARGET_PREFIX}" PLAT=tegra ${TARGET_SOC_OEMAKE} \
	        DEBUG=${ATF_DEBUG} LOG_LEVEL=${ATF_LOG_LEVEL} V=1 TARGET_SOC=${TARGET_SOC} \
	        ${BUILDTIMESTAMP} ${BUILD_STRING}'

do_configure[noexec] = "1"

do_compile() {
	oe_runmake -C ${S} all
        dtc -I dts -O dtb -o ${B}/tegra/${TARGET_SOC}/release/nvidia-${TARGET_SOC}.dtb ${S}/fdts/nvidia-${TARGET_SOC}.dts
	fiptool create --soc-fw ${B}/tegra/${TARGET_SOC}/release/bl31.bin \
                       --soc-fw-config ${B}/tegra/${TARGET_SOC}/release/nvidia-${TARGET_SOC}.dtb \
                       ${B}/tegra/${TARGET_SOC}/release/bl31.fip
}
do_compile[cleandirs] = "${B}"

do_install() {
	install -d ${D}${datadir}/trusted-os
	install -m 0644 ${B}/tegra/${TARGET_SOC}/release/bl31.fip ${D}${datadir}/trusted-os/
}

ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-dev = "${datadir}/trusted-os"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
