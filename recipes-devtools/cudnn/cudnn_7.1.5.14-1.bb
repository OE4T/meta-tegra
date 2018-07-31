SUMMARY = "NVIDIA CUDA Deep Neural Network library"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/cudnn_v7.h;endline=48;md5=b48d68d7e5eb6b858c229fdb89171636"

L4T_URI_BASE = "https://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/3.3/lw.xd42/JetPackL4T_33_b39"
SRC_URI = "\
    ${L4T_URI_BASE}/libcudnn7_${PV}+cuda9.0_arm64.deb;name=lib;unpack=false \
    ${L4T_URI_BASE}/libcudnn7-dev_${PV}+cuda9.0_arm64.deb;name=dev;unpack=false \
    ${L4T_URI_BASE}/libcudnn7-doc_${PV}+cuda9.0_arm64.deb;name=doc;unpack=false \
"
SRC_URI[lib.md5sum] = "b846bf9573a5cee7fc29f22171fc3cd0"
SRC_URI[lib.sha256sum] = "a7bb58b54827e8409455d8518c756e93b87138de1f80e8a218b624d71ec243ee"
SRC_URI[dev.md5sum] = "54123b591320ff9157290b17c5d340b4"
SRC_URI[dev.sha256sum] = "52602d454886ddb19ea196375370841b78ca16a5eb8bb16af1dda47cddd81225"
SRC_URI[doc.md5sum] = "7f8fc27dc9a31d5f1740c941b90c0d9c"
SRC_URI[doc.sha256sum] = "10372372772fd516d1c92ec915fd2d97b70df06f469d4ed442c21b86390113fe"

COMPATIBLE_MACHINE = "(tegra186|tegra210)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

PR = "r0"

DEPENDS = "dpkg-native"

def extract_basever(d):
    ver = d.getVar('PV').split('-')[0]
    components = ver.split('.')
    return '%s.%s.%s' % (components[0], components[1], components[2])

BASEVER = "${@extract_basever(d)}"

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
    install -m 0644 ${B}/usr/lib/aarch64-linux-gnu/libcudnn.so.${BASEVER} ${D}${libdir}/
    install -m 0644 ${B}/usr/lib/aarch64-linux-gnu/libcudnn_static_v7.a ${D}${libdir}/
    ln -s libcudnn.so.${BASEVER} ${D}${libdir}/libcudnn.so.7
    ln -s libcudnn.so.${BASEVER} ${D}${libdir}/libcudnn.so
    cp --preserve=mode,timestamps --recursive ${S}/usr/share/* ${D}${datadir}/
    rm -rf ${D}${datadir}/lintian
    cp --preserve=mode,timestamps --recursive ${S}/usr/src/* ${D}${prefix}/src/
}

PACKAGES =+ "${PN}-samples"
FILES_${PN}-samples = "${prefix}/src"
INSANE_SKIP_${PN} = "ldflags"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

RDEPENDS_${PN}-samples = "cuda-cublas-dev cuda-cudart"
RPROVIDES_${PN}-samples = "${PN}-examples"
INSANE_SKIP_${PN}-samples = "build-deps dev-deps ldflags staticdev"
