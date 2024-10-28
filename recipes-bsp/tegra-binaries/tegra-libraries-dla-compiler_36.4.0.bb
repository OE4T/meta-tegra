SUMMARY = "NVIDIA DLA Compiler"
HOMEPAGE = "http://developer.nvidia.com/jetson"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/nvidia-l4t-dla-compiler/copyright;md5=45d9a257d98fb290f71e4f5f87f805b4"

inherit l4t_deb_pkgfeed

SRC_COMMON_DEBS = "nvidia-l4t-dla-compiler_${PV}_arm64.deb;subdir=${BPN}"
PV .= "${@l4t_bsp_debian_version_suffix(d, pkgname='nvidia-l4t-dla-compiler')}"

SRC_URI[sha256sum] = "31cd17e859590332c17e26ed0b6d3ca4dce604a366b5b724cad89a08fd1b368a"

COMPATIBLE_MACHINE = "(tegra)"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"

S = "${WORKDIR}/${BPN}"
B = "${S}"

COMPATIBLE_MACHINE = "(tegra)"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}${libdir}
    install -m 0644 usr/lib/aarch64-linux-gnu/nvidia/libnvdla_compiler.so ${D}${libdir}
}

RDEPENDS:${PN} = "tegra-libraries-core"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
INSANE_SKIP:${PN} = "already-stripped"
