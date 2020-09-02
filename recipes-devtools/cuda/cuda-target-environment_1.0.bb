DESCRIPTION = "SDK environment setup for CUDA targets"
LICENSE = "MIT"
HOMEPAGE = "https://github.com/OE4T/meta-tegra"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://cuda_target.sh.in"

COMPATIBLE_MACHINE = "(cuda)"

S = "${WORKDIR}"

COMPILER_CMD  = "${@d.getVar('CXX').split()[0]}"

def arch_flags(d):
    archflags = d.getVar('TARGET_CC_ARCH')
    if archflags:
        return "-Xcompiler " + ','.join(archflags.split())
    return ""

do_compile() {
    sed -e"s!@CUDA_NVCC_ARCH_FLAGS@!${CUDA_NVCC_ARCH_FLAGS}!" \
	-e"s!@ARCHFLAGS@!${@arch_flags(d)}!" \
	-e"s!@COMPILER_CMD@!${COMPILER_CMD}!" ${S}/cuda_target.sh.in > ${B}/cuda_target.sh
}

do_install() {
    install -d ${D}/environment-setup.d
    install -m 0644 ${B}/cuda_target.sh ${D}/environment-setup.d/
}

FILES_${PN} = "/environment-setup.d"
PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"
