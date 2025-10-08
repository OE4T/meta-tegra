SUMMARY = "Hafnium - L4T distribution for Tegra264"
DESCRIPTION = "A reference Secure Partition Manager (SPM) for systems that \
  implement the Armv8.5-A Secure-EL2 extension"
HOMEPAGE = "https://www.trustedfirmware.org/projects/hafnium"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=782b40c14bad5294672c500501edc103"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit deploy

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/hafnium_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-38.2.1.inc

SRC_URI:append = " \
    file://0001-arm-hafnium-fix-kernel-tool-linking.patch;patchdir=.. \
    file://0002-work-around-visibility-issue.patch;patchdir=.. \
    file://0003-fix-build-with-clang-17-and-recent.patch;patchdir=.. \
    file://0004-Updates-for-OE-cross-builds.patch;patchdir=.. \
    file://0005-drop-test_root-group-and-unit_tests-executable.patch;patchdir=.. \
"

S = "${UNPACKDIR}/hafnium"
B = "${WORKDIR}/build"

DEPENDS = "gn-native ninja-native bison-native bc-native dtc-native openssl-native clang-native lld-native l4t-atf-tools-native"

COMPATIBLE_MACHINE = "(tegra264)"

HAFNIUM_PROJECT ?= "${S}/soc/t264"
HAFNIUM_PLATFORM ?= "t264"

# do_deploy will install everything listed in this variable. It is set by
# default to hafnium
HAFNIUM_INSTALL_TARGET ?= "hafnium"

# set project to build
EXTRA_OEMAKE += "PROJECT=${HAFNIUM_PROJECT} OUT_DIR=${B}"

# Don't use prebuilt binaries for gn and ninja
EXTRA_OEMAKE += "GN=${STAGING_BINDIR_NATIVE}/gn NINJA=${STAGING_BINDIR_NATIVE}/ninja"

do_configure[cleandirs] += "${B}"

do_compile() {
    oe_runmake -C ${S}
}

do_compile:append() {
    dtc -I dts -O dtb -o ${B}/t264_spmc.dtb ${HAFNIUM_PROJECT}/manifests/t264_spmc.dts0
}

do_deploy() {
    ${STAGING_BINDIR_NATIVE}/fiptool create \
        --tos-fw ${B}/nvidia_t264_clang/hafnium.bin \
        --tos-fw-config ${B}/t264_spmc.dtb \
        ${DEPLOYDIR}/hafnium_t264.fip
}
addtask deploy before do_build after do_compile
