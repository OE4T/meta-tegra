SUMMARY = "NVIDIA DLA Compiler"
HOMEPAGE = "http://developer.nvidia.com/jetson"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/nvidia-l4t-dla-compiler/copyright;md5=081a55629f95871e54b51df115ad510e"

inherit l4t_deb_pkgfeed

SRC_SOC_DEBS = "nvidia-l4t-dla-compiler_${PV}_arm64.deb;subdir=${BPN}"
PV .= "${@l4t_bsp_debian_version_suffix(d, pkgname='nvidia-l4t-dla-compiler')}"

SRC_URI[sha256sum] = "27e8f7cf0c627728ef72d1611f05466f57dd20b50c2c8c806d667ae31cb97012"

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
