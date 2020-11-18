LICENSE = "MIT & BSD-2-Clause & BSD-3-Clause & Apache-2.0 & Zlib & Proprietary"
LIC_FILES_CHKSUM = " \
    file://bootloader/partner/t18x/cboot/target/init.c;endline=22;md5=59dc0a752bc93e442d4ec3de8b1d8614 \
    file://bootloader/partner/t18x/cboot/kernel/semaphore.c;endline=15;md5=ff935df987c4efa30f6c5462f708f1a4 \
    file://bootloader/partner/common/lib/external/mbedtls/bignum.c;endline=35;md5=84d5feb35adf77539b8a50b9497ea92a \
    file://bootloader/partner/common/lib/external/asn1/asn1_decoder.c;endline=23;md5=3f072eb94ef35fa574edc8b2dd5931c1 \
    file://bootloader/partner/common/lib/external/lz4/lz4.c;endline=33;md5=374dbaf7e07d3845e06cbab8589578e9 \
    file://bootloader/partner/common/lib/external/mincrypt/sha.c;endline=34;md5=4a0fce84ffc9e3901a9fb2e2a290e7b8 \
    file://bootloader/partner/common/lib/external/zlib/zlib.h;beginline=6;endline=23;md5=5377232268e952e9ef63bc555f7aa6c0 \
    file://LICENSE.cboot.txt;md5=972762a86d83ebd2df0b07be8d084935 \
"

inherit l4t_bsp

L4T_URI_BASE = "https://developer.download.nvidia.com/embedded/L4T/r32_Release_v4.4"
SRC_TARBALL ??= "INVALID"

PROVIDES += "cboot"

S = "${WORKDIR}/${BP}"
B = "${WORKDIR}/build"

TARGET_SOC ??= "INVALID"

EXTRA_GLOBAL_DEFINES = "${PACKAGECONFIG_CONFARGS}"
EXTRA_OEMAKE = 'TEGRA_TOP=${S} GLOBAL_CFLAGS=${CBOOT_CFLAGS} CFLAGS= CPPFLAGS= LDFLAGS= PROJECT=${TARGET_SOC} TOOLCHAIN_PREFIX="${TARGET_PREFIX}" DEBUG=2 \
                BUILDROOT="${B}" NV_TARGET_BOARD=t194ref NV_BUILD_SYSTEM_TYPE=l4t EXTRA_GLOBAL_DEFINES="${EXTRA_GLOBAL_DEFINES}"'

CBOOT_CFLAGS ?= ""
CBOOT_BUILD_ARTIFACT ?= "build-${TARGET_SOC}/lk.bin"
CBOOT_IMAGE ?= "cboot-${MACHINE}-${PV}-${PR}.bin"
CBOOT_SYMLINK ?= "cboot-${MACHINE}.bin"

inherit python3native deploy nopackages

def cboot_version_number(d):
    ver = d.getVar('PV').split('.')
    return '%02d.%02d.%02d' % (int(ver[0]), int(ver[1]), int(ver[2]))

CBOOT_VERSION ?= "${@cboot_version_number(d)}${TARGET_VENDOR}"

do_configure() {
    sed -i -r -e'/^BUILD_BRANCH/d' -e'/^BUILD_DATE/d' \
              -e's,^(BUILD_VERSION :=).*,BUILD_VERSION := ${CBOOT_VERSION},' \
        ${S}/bootloader/partner/t18x/cboot/build/version.mk
}
do_configure[cleandirs] = "${B}"

do_compile() {
    LIBGCC=`${CC} -print-libgcc-file-name`
    oe_runmake -C ${S}/bootloader/partner/t18x/cboot LIBGCC="$LIBGCC"
}

do_install() {
	:
}

do_deploy() {
	install -d ${DEPLOYDIR}
	install -m 0644 ${B}/${CBOOT_BUILD_ARTIFACT} ${DEPLOYDIR}/${CBOOT_IMAGE}
	ln -sf ${CBOOT_IMAGE} ${DEPLOYDIR}/${CBOOT_SYMLINK}
}

addtask deploy before do_build after do_install

PACKAGE_ARCH = "${MACHINE_ARCH}"
