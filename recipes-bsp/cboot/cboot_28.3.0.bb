DESCRIPTION = "cboot bootloader for Tegra186"

LICENSE = "MIT & BSD-2-Clause & BSD-3-Clause & Apache-2.0 & Zlib & Proprietary"
LIC_FILES_CHKSUM = " \
    file://bootloader/partner/t18x/cboot/target/init.c;endline=22;md5=59dc0a752bc93e442d4ec3de8b1d8614 \
    file://bootloader/partner/t18x/cboot/kernel/semaphore.c;endline=15;md5=ff935df987c4efa30f6c5462f708f1a4 \
    file://bootloader/partner/common/lib/external/mbedtls/bignum.c;endline=35;md5=84d5feb35adf77539b8a50b9497ea92a \
    file://bootloader/partner/common/lib/external/asn1/asn1_decoder.c;endline=23;md5=3f072eb94ef35fa574edc8b2dd5931c1 \
    file://bootloader/partner/common/lib/external/lz4/lz4.c;endline=33;md5=374dbaf7e07d3845e06cbab8589578e9 \
    file://bootloader/partner/common/lib/external/mincrypt/sha.c;endline=34;md5=4a0fce84ffc9e3901a9fb2e2a290e7b8 \
    file://bootloader/partner/common/lib/external/zlib/zlib.h;beginline=6;endline=23;md5=5377232268e952e9ef63bc555f7aa6c0 \
    file://bootloader/partner/common/drivers/display/tegrabl_display.c;endline=9;md5=712b8171bd980f548ca70c3a4e59ede3 \
"

SRC_URI = "${NVIDIA_DEVNET_MIRROR}/L4T-${PV}/cboot_src.tbz2;subdir=${BP};downloadfilename=${BP}.tbz2"
SRC_URI[md5sum] = "60001d8707487462c0f2376e912d71d9"
SRC_URI[sha256sum] = "5578efc189fbedaa0dd87d423755fec37a123fab62a2ff9d9dbe8b3363f429c0"

SRC_URI += " \
    file://0001-tegrabl_sor-fix-register-bitmask-reference.patch \
    file://0002-tegrabl_qspi_flash-fix-maybe-uninitialized-errors.patch \
    file://0003-tegrabl_bootloader_update-fix-int-conversion-error.patch \
    file://0004-tegrabl_clk_bpmp-fix-switch-bool-compilation-error.patch \
    file://0005-tegrabl_xusbf-fix-misleading-indentation-compilation.patch \
    file://0006-zlib-fix-shift-negative-value-compilation-error.patch \
    file://0007-make-update-MAKECONFIGHEADER-macro.patch \
    file://0008-disable-implicit-fallthrough-compilation-errors.patch \
"

COMPATIBLE_MACHINE = "(tegra186)"
PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/${BP}"
B = "${WORKDIR}/build"

EXTRA_OEMAKE = 'CFLAGS= CPPFLAGS= LDFLAGS= PROJECT=t186 TOOLCHAIN_PREFIX="${TARGET_PREFIX}" DEBUG=2 BUILDROOT="${B}" NV_BUILD_SYSTEM_TYPE=l4t NOECHO=" "'

CBOOT_IMAGE ?= "cboot-${MACHINE}-${PV}-${PR}.bin"
CBOOT_SYMLINK ?= "cboot-${MACHINE}.bin"

inherit pythonnative nvidia_devnet_downloads deploy

do_compile() {
    LIBGCC=`${CC} -print-libgcc-file-name`
    oe_runmake -C ${S}/bootloader/partner/t18x/cboot LIBGCC="$LIBGCC"
}

do_install() {
	:
}

do_deploy() {
	install -d ${DEPLOYDIR}
	install -m 0644 ${B}/build-t186/lk.bin ${DEPLOYDIR}/${CBOOT_IMAGE}
	ln -sf ${CBOOT_IMAGE} ${DEPLOYDIR}/${CBOOT_SYMLINK}
}

addtask deploy before do_build after do_install

python () {
    socarch = d.getVar("SOC_FAMILY") or ""
    if socarch in ['tegra186', 'tegra194'] and d.getVar("PREFERRED_PROVIDER_virtual/bootloader").startswith("cboot"):
        d.appendVar("PROVIDES", " virtual/bootloader")
}
