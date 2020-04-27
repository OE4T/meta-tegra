SUMMARY = "Trusty secure OS - L4T distribution"
DESCRIPTION = "Trusty is a secure Operating System (OS) that provides a \
Trusted Execution Environment (TEE) for Android."
HOMEPAGE = "//https://source.android.com/security/trusty"
LICENSE = "MIT & BSD-3-Clause & Apache-2.0"
LIC_FILES_CHKSUM = "\
    file://tegra/public/LICENSE;md5=7100b53ab2e3231bd0ba50d55a53fbef \
    file://lib/include/LICENSE;md5=1163d440b7d4a6defb52a783d445ff49 \
    file://external/headers/include/remoteproc/LICENSE;md5=6938907077867979a4d0a7fd30f85aed \
    file://app/sample/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
    file://app/sample/hwkey-agent/LICENSE;md5=0f2184456a07e1ba42a53d9220768479 \
    file://lk/trusty/LICENSE;md5=1163d440b7d4a6defb52a783d445ff49 \
    file://lk/common/LICENSE;md5=25394d472e4c06f4d61140e88861fb5b \
"

DEPENDS = "gcc-linaro-baremetal-arm-native"

B = "${WORKDIR}/build"

CFLAGS[unexport] = "1"
LDFLAGS[unexport] = "1"
AS[unexport] = "1"
LD[unexport] = "1"

TARGET_SOC = "t186"
TARGET_SOC_tegra194 = "t194"
PATH_prepend = "${B}/bin-${BUILD_ARCH}:${STAGING_DATADIR_NATIVE}/gcc-linaro-baremetal-arm/bin:"

make_tc_wrappers() {
    tgtdir="$1"
    shift
    while [ -n "$1" ]; do
        cat > $tgtdir/${HOST_PREFIX}$1 <<EOF
#!/bin/sh
exec ${STAGING_BINDIR_TOOLCHAIN}/${HOST_PREFIX}$1 ${TOOLCHAIN_OPTIONS} "\$@"
EOF
        chmod +x $tgtdir/${HOST_PREFIX}$1
        shift
    done
}
   
do_configure() {
    rm -rf ${B}/bin-${BUILD_ARCH}
    mkdir ${B}/bin-${BUILD_ARCH}
    make_tc_wrappers ${B}/bin-${BUILD_ARCH} as cpp gcc ld ld.bfd
}


EXTRA_OEMAKE = 'PROJECT=t186 TARGET=t186 BUILDROOT=${B} \
                TOOLCHAIN_PREFIX="${TARGET_PREFIX}" ARCH_arm64_TOOLCHAIN_PREFIX="${TARGET_PREFIX}" \
		ARCH_arm_TOOLCHAIN_PREFIX="arm-eabi-" \
	        DEBUG=0 DEBUG_LVL=0 V=0 DEFAULT_OTE_APP_DEBUGLEVEL=1 NOECHO=@ \
		TRUSTY_VARIANT=l4t-public TRUSTY_MULTI_GUEST_CONFIGURATION= \
		TARGET_SOC=${TARGET_SOC}'

do_compile() {
    oe_runmake -C ${S} t186
}

do_install() {
    install -d ${D}${datadir}/trusted-os
    install -m 0644 ${B}/build-t186/lk.bin ${D}${datadir}/trusted-os/
}

ALLOW_EMPTY_${PN} = "1"
FILES_${PN}-dev = "${datadir}/trusted-os"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
