SUMMARY = "NVIDIA CUDA Deep Neural Network library"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/cudnn_v7.h;endline=48;md5=b48d68d7e5eb6b858c229fdb89171636"

SRC_URI = "\
    http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/3.2GA/m892ki/JetPackL4T_32_b196/libcudnn7_${PV}+cuda9.0_arm64.deb;name=lib;unpack=false \
    http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/3.2GA/m892ki/JetPackL4T_32_b196/libcudnn7-dev_${PV}+cuda9.0_arm64.deb;name=dev;unpack=false \
    http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/3.2GA/m892ki/JetPackL4T_32_b196/libcudnn7-doc_${PV}+cuda9.0_arm64.deb;name=doc;unpack=false \
"
SRC_URI[lib.md5sum] = "87f5520d4c8f36d98de4a567e6528538"
SRC_URI[lib.sha256sum] = "a0a1cd124953ec9c64c942e1da2f55825fb6da2583a4900d248ad978b83a04ad"
SRC_URI[dev.md5sum] = "6242f55784ddf1e8123ff1b024440bc1"
SRC_URI[dev.sha256sum] = "c53783f6ebd01b510626c0709313425483d1bf6a929d80740ded664fe5a00293"
SRC_URI[doc.md5sum] = "fcab6c8c39183de830d49c4c277851ba"
SRC_URI[doc.sha256sum] = "774c2b281be360f6177cc69787c97e02da5ba89940deded6f955e85e4d7a65f0"

COMPATIBLE_MACHINE = "(tegra186|tegra210)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

PR = "r0"

DEPENDS = "dpkg-native"

S = "${WORKDIR}/cudnn"

do_configure() {
    dpkg-deb --extract ${WORKDIR}/libcudnn7_${PV}+cuda9.0_arm64.deb ${S}
    dpkg-deb --extract ${WORKDIR}/libcudnn7-dev_${PV}+cuda9.0_arm64.deb ${S}
    dpkg-deb --extract ${WORKDIR}/libcudnn7-doc_${PV}+cuda9.0_arm64.deb ${S}
}
do_populate_lic[depends] += "${PN}:do_configure"

do_compile() {
    :
}

do_install() {
    install -d ${D}${includedir} ${D}${libdir} ${D}${datadir} ${D}${prefix}/src
    install -m 0644 ${S}/usr/include/aarch64-linux-gnu/*.h ${D}${includedir}
    install -m 0644 ${B}/usr/lib/aarch64-linux-gnu/libcudnn.so.7.0.5 ${D}${libdir}/
    install -m 0644 ${B}/usr/lib/aarch64-linux-gnu/libcudnn_static_v7.a ${D}${libdir}/
    ln -s libcudnn.so.7.0.5 ${D}${libdir}/libcudnn.so.7
    ln -s libcudnn.so.7.0.5 ${D}${libdir}/libcudnn.so
    cp --preserve=mode,timestamps --recursive ${S}/usr/share/* ${D}${datadir}/
    rm -rf ${D}${datadir}/lintian
    cp --preserve=mode,timestamps --recursive ${S}/usr/src/* ${D}${prefix}/src/
}

PACKAGES =+ "${PN}-examples"
FILES_${PN}-examples = "${prefix}/src"
INSANE_SKIP_${PN} = "ldflags"
INSANE_SKIP_${PN}-examples = "ldflags staticdev"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

RDEPENDS_${PN}-examples = "cuda-cublas-dev cuda-cudart"
INSANE_SKIP_${PN}-examples = "build-deps dev-deps ldflags staticdev"
