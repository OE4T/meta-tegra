SUMMARY = "Vision Programming Interface(VPI) is an API for accelerated \
  computer vision and image processing for embedded systems."
HOMEPAGE = "https://developer.nvidia.com/embedded/vpi"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = " \
    file://etc/ld.so.conf.d/vpi1.conf;md5=bfdc8e915558abe115e0fc762a2ff724 \
    file://opt/nvidia/vpi1/include/vpi/VPI.h;endline=48;md5=7c9511c3e53f3d844d189edd66c44682"

inherit l4t_deb_pkgfeed features_check

SRC_COMMON_DEBS = "\
    libnvvpi1_${PV}_arm64.deb;name=lib;subdir=vpi1 \
    vpi1-dev_${PV}_arm64.deb;name=dev;subdir=vpi1 \
"
L4T_DEB_GROUP[dev] = "vpi1-dev"
SRC_URI[lib.sha256sum] = "96bf55201147b6d687a740f5201c025e89f02568b2ea67ec9ceee5c7a262ab63"
SRC_URI[dev.sha256sum] = "0afeddb485916fc401de8b600cf9a44b24ec197060e1f07e4384bc498181316d"

SRC_URI:append = " file://0001-vpi-config-allow-to-compute-the-installation-prefix.patch"

REQUIRED_DISTRO_FEATURES = "opengl"

S = "${WORKDIR}/vpi1"
B = "${S}"

DEPENDS = "cuda-cudart libcufft tegra-libraries-multimedia-utils tegra-libraries-multimedia tegra-libraries-eglcore"
SYSROOT_DIRS:append = " /opt"

COMPATIBLE_MACHINE = "(tegra)"

do_compile() {
    :
}

do_install() {
    install -d ${D}/opt/nvidia/vpi1/lib64
    cp -R --preserve=mode,timestamps,links --no-dereference ${B}/opt/nvidia/vpi1/lib64/* ${D}/opt/nvidia/vpi1/lib64/
    install -d ${D}/${sysconfdir}/ld.so.conf.d/
    cp --preserve=mode,timestamps ${B}/etc/ld.so.conf.d/vpi1.conf ${D}/${sysconfdir}/ld.so.conf.d/
    install -d  ${D}/opt/nvidia/vpi1/include
    cp -R --preserve=mode,timestamps ${B}/opt/nvidia/vpi1/include/* ${D}/opt/nvidia/vpi1/include/
    install -d ${D}${datadir}
    cp -R --preserve=mode,timestamps ${B}/usr/share/vpi1 ${D}${datadir}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

PACKAGES = "${PN} ${PN}-dev"
FILES:${PN} = "/opt/nvidia/vpi1/lib64/libnvvpi.so.1* ${sysconfdir}/ld.so.conf.d"
FILES:${PN}-dev = "/opt/nvidia/vpi1/lib64/libnvvpi.so /opt/nvidia/vpi1/include ${datadir}/vpi1/cmake"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
