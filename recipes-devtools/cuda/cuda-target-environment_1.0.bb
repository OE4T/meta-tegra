DESCRIPTION = "SDK environment setup for CUDA targets"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://cuda_target.sh.in"

COMPATIBLE_MACHINE = "(cuda)"

inherit cuda-gcc

S = "${UNPACKDIR}"

COMPILER_CMD  = "${@d.getVar('CXX_FOR_CUDA').split()[0]}"
CMAKE_CUDA_ARCHITECTURES = "${@d.getVar('CUDA_ARCHITECTURES') if d.getVar('CUDA_ARCHITECTURES') else 'OFF'}"
CUDA_EXTRA_CXXFLAGS ??= "-isystem=${includedir}/cuda-compat-workarounds"

def arch_flags(d):
    archflags = (d.getVar('TARGET_CC_ARCH') or '').split() + (d.getVar('CUDA_EXTRA_CXXFLAGS') or '').split()
    if archflags:
        return "-Xcompiler " + ','.join(archflags)
    return ""

do_compile() {
    sed -e"s!@CUDA_NVCC_ARCH_FLAGS@!${CUDA_NVCC_ARCH_FLAGS}!" \
        -e"s!@ARCHFLAGS@!${@arch_flags(d)}!" \
        -e"s!@CUDA_ARCHITECTURES@!${CMAKE_CUDA_ARCHITECTURES}!" \
        -e"s!@COMPILER_CMD@!${COMPILER_CMD}!" ${S}/cuda_target.sh.in > ${B}/cuda_target.sh
}

do_install() {
    install -d ${D}/environment-setup.d
    install -m 0644 ${B}/cuda_target.sh ${D}/environment-setup.d/
}

FILES:${PN} = "/environment-setup.d"
RDEPENDS:${PN} = "tegra-cmake-overrides"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
