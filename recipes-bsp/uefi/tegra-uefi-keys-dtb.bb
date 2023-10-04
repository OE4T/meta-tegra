DESCRIPTION = "Generates DTB overlay containing UEFI certificate information for Tegra platforms"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(tegra)"

# Add a bbappend to this recipe to supply a
# non-empty file containing the key information,
# as generated by gen_uefi_keys_dts.sh
# in the L4T kit.  You are only required to
# provide UefiUpdateSecurityKeys.dts if you
# choose to update your uefi keys.
SRC_URI = "\
    file://UefiDefaultSecurityKeys.dts \
"

inherit deploy nopackages

DEPENDS = "dtc-native"

B = "${WORKDIR}/build"

do_configure() {
    if [ ! -s "${WORKDIR}/UefiDefaultSecurityKeys.dts" ]; then
        bbfatal "Please provide a non-empty UefiDefaultSecurityKeys.dts"
    fi
}

do_compile() {
    dtc -Idts -Odtb -o ${B}/UefiDefaultSecurityKeys.dtbo ${WORKDIR}/UefiDefaultSecurityKeys.dts
    if [ -a "${WORKDIR}/UefiUpdateSecurityKeys.dts" ]; then
        dtc -Idts -Odtb -o ${B}/UefiUpdateSecurityKeys.dtbo ${WORKDIR}/UefiUpdateSecurityKeys.dts
    fi
}

do_install[noexec] = "1"

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${B}/UefiDefaultSecurityKeys.dtbo ${DEPLOYDIR}/
    if [ -a "${B}/UefiUpdateSecurityKeys.dtbo" ]; then
        install -m 0644 ${B}/UefiUpdateSecurityKeys.dtbo ${DEPLOYDIR}/
    fi
}

addtask deploy before do_build after do_compile

PACKAGE_ARCH = "${MACHINE_ARCH}"
