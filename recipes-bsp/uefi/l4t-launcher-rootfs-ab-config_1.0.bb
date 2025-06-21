DESCRIPTION = "L4T Launcher DTB overlay for enabling rootfs A/B redundancy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(tegra)"

DEPENDS = "dtc-native"
SRC_URI = "file://L4TConfiguration-RootfsRedundancyLevelABEnable.dtsi"
S = "${UNPACKDIR}"

inherit deploy

do_configure() {
    :
}

do_compile() {
       dtc -Idts -Odtb -o ${B}/L4TConfiguration-RootfsRedundancyLevelABEnable.dtbo ${UNPACKDIR}/L4TConfiguration-RootfsRedundancyLevelABEnable.dtsi
}

do_install() {
    :
}

do_deploy() {
    install -D -m 0644 -t ${DEPLOYDIR} ${B}/L4TConfiguration-RootfsRedundancyLevelABEnable.dtbo
}

addtask deploy before do_build after do_install
