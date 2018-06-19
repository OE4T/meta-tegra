SUMMARY = "NVIDIA CUDA Deep Neural Network library"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/cudnn_v7.h;endline=48;md5=b48d68d7e5eb6b858c229fdb89171636"

L4T_URI_BASE = "https://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/3.2.1/m8u2ki/JetPackL4T_321_b23"
SRC_URI = "\
    ${L4T_URI_BASE}/libcudnn7_${PV}+cuda9.0_arm64.deb;name=lib;unpack=false \
    ${L4T_URI_BASE}/libcudnn7-dev_${PV}+cuda9.0_arm64.deb;name=dev;unpack=false \
    ${L4T_URI_BASE}/libcudnn7-doc_${PV}+cuda9.0_arm64.deb;name=doc;unpack=false \
"
SRC_URI[lib.md5sum] = "6aab1d77a60871a0ee0d306aee6f9a0e"
SRC_URI[lib.sha256sum] = "494e51b27e308b1f8074d805e7657953086a05501f2c65a573cbfade2da0ffb8"
SRC_URI[dev.md5sum] = "919feccd183273cd3b1efab1aca39f04"
SRC_URI[dev.sha256sum] = "9d4a4b421522ae1c4c0443a2a70e33193de75d234b0325610bec1bf396c9594a"
SRC_URI[doc.md5sum] = "f1746b71c84cf42542a42e9b52e03f2d"
SRC_URI[doc.sha256sum] = "9717956eafda464a37399692910699ce9b5a63c581849a74158cd92670c2e477"

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
