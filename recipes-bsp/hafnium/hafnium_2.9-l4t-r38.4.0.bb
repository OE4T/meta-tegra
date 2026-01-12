SUMMARY = "Hafnium - L4T distribution for Tegra264"
DESCRIPTION = "A reference Secure Partition Manager (SPM) for systems that \
  implement the Armv8.5-A Secure-EL2 extension"
HOMEPAGE = "https://www.trustedfirmware.org/projects/hafnium"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://hafnium/LICENSE;md5=782b40c14bad5294672c500501edc103"

inherit deploy

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/hafnium_src.tbz2"
TEGRA_SRC_SUBARCHIVE_OPTS = "-C ${S}"
require recipes-bsp/tegra-sources/tegra-sources-38.4.0.inc

SRC_URI += "file://0001-work-around-visibility-issue.patch \
           file://0002-fix-build-with-clang-17-and-recent.patch \
           file://0003-aarch64-fix-use-of-uninitialized-pointer.patch \
           file://0004-googletest-import-fix-for-compilation-errors.patch \
           file://0005-toolchain-host-add-BUILD_LDFLAGS.patch \
           file://0006-Fix-clang-18-compilation-errors.patch \
           "

S = "${UNPACKDIR}/hafnium"
B = "${WORKDIR}/build"

DEPENDS = "gn-native ninja-native bison-native bc-native dtc-native openssl-native lld-native l4t-atf-tools-native libcxx"

COMPATIBLE_MACHINE = "(tegra264)"
TOOLCHAIN_NATIVE = "clang"

HAFNIUM_PROJECT ?= "${S}/hafnium/soc/t264"

# set project to build
EXTRA_OEMAKE += "PROJECT=${HAFNIUM_PROJECT} OUT_DIR=${B} HAFNIUM_ROOT=${S}"

# Don't use prebuilt binaries for gn and ninja
EXTRA_OEMAKE += "GN=${STAGING_BINDIR_NATIVE}/gn NINJA=${STAGING_BINDIR_NATIVE}/ninja"

do_unpack[dirs] += "${S}"
do_configure[cleandirs] += "${B}"

do_compile() {
    oe_runmake -C ${S}/hafnium

    ${CPP} -nostdinc -undef -x assembler-with-cpp -I ${HAFNIUM_PROJECT}/manifests/include ${HAFNIUM_PROJECT}/manifests/t264_spmc.dts > ${B}/t264_spmc.dts
    dtc -I dts -O dtb -o ${B}/t264_spmc.dtb ${B}/t264_spmc.dts
}

do_deploy() {
    ${STAGING_BINDIR_NATIVE}/fiptool create \
        --tos-fw ${B}/nvidia_t264_clang/hafnium.bin \
        --tos-fw-config ${B}/t264_spmc.dtb \
        ${DEPLOYDIR}/hafnium_t264.fip
}
addtask deploy before do_build after do_compile

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
