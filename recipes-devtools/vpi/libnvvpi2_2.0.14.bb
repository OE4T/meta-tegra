SUMMARY = "Vision Programming Interface(VPI) is an API for accelerated \
  computer vision and image processing for embedded systems."
HOMEPAGE = "https://developer.nvidia.com/embedded/vpi"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = " \
    file://etc/ld.so.conf.d/vpi2.conf;md5=65d38504d4d463d2257a0a1ac84cb147 \
    file://opt/nvidia/vpi2/include/vpi/VPI.h;endline=48;md5=7c9511c3e53f3d844d189edd66c44682"

inherit l4t_deb_pkgfeed features_check

SRC_COMMON_DEBS = "\
    libnvvpi2_${PV}_arm64.deb;name=lib;subdir=vpi2 \
    vpi2-dev_${PV}_arm64.deb;name=dev;subdir=vpi2 \
"
L4T_DEB_GROUP[dev] = "vpi2-dev"
SRC_URI[lib.sha256sum] = "4c8866ba360917f95866e79e2519cfb23ace13e9be9152f1612948ac9405352a"
SRC_URI[dev.sha256sum] = "6bc1ababc6d4b993c8589177503ea4c7a5a77556a971707eec61b3d397b31307"

SRC_URI:append = " file://0001-vpi-config-allow-to-compute-the-installation-prefix.patch"

REQUIRED_DISTRO_FEATURES = "opengl"

S = "${WORKDIR}/vpi2"
B = "${S}"

DEPENDS = "cuda-cudart libcufft tegra-libraries-multimedia-utils tegra-libraries-multimedia tegra-libraries-eglcore \
           tegra-libraries-pva tegra-libraries-nvsci"
# XXX--- see hack in do_install
DEPENDS += "patchelf-native"
# ---XXX
SYSROOT_DIRS:append = " /opt"

COMPATIBLE_MACHINE = "(tegra)"

def extract_basever(d):
    ver = d.getVar('PV').split('-')[0]
    components = ver.split('.')
    return '%s.%s.%s' % (components[0], components[1], components[2])

def extract_majver(d):
    ver = d.getVar('PV').split('-')[0]
    return ver.split('.')[0]

BASEVER = "${@extract_basever(d)}"
MAJVER = "${@extract_majver(d)}"

do_compile() {
    :
}

do_install() {
    install -d ${D}/opt/nvidia/vpi2/lib64 ${D}/opt/nvidia/vpi2/lib64/priv
    install -m 0644 ${B}/opt/nvidia/vpi2/lib64/libnvvpi.so.${BASEVER} ${D}/opt/nvidia/vpi2/lib64
    ln -s libnvvpi.so.${BASEVER} ${D}/opt/nvidia/vpi2/lib64/libnvvpi.so.${MAJVER}
    ln -s libnvvpi.so.${MAJVER} ${D}/opt/nvidia/vpi2/lib64/libnvvpi.so
    install -m 0644 ${B}/opt/nvidia/vpi2/lib64/priv/libcupva_host.so ${D}/opt/nvidia/vpi2/lib64/priv
    # XXX---
    # libcupva_host.so is not using the actual SONAME for libnvscibuf
    # in its DT_NEEDED ELF header, so we have to rewrite it to prevent
    # a broken runtime dependency.
    patchelf --replace-needed libnvscibuf.so libnvscibuf.so.1 ${D}/opt/nvidia/vpi2/lib64/priv/libcupva_host.so
    # ---XXX
    install -m 0644 ${B}/opt/nvidia/vpi2/lib64/priv/libcupva_host_utils.so ${D}/opt/nvidia/vpi2/lib64/priv

    install -d ${D}${sysconfdir}/ld.so.conf.d/
    cp --preserve=mode,timestamps ${B}/etc/ld.so.conf.d/vpi2.conf ${D}${sysconfdir}/ld.so.conf.d/
    install -d  ${D}/opt/nvidia/vpi2/include
    cp -R --preserve=mode,timestamps ${B}/opt/nvidia/vpi2/include/* ${D}/opt/nvidia/vpi2/include/
    install -d ${D}${datadir}
    cp -R --preserve=mode,timestamps ${B}/usr/share/vpi2 ${D}${datadir}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

PACKAGES = "${PN} ${PN}-dev"
FILES:${PN} = "/opt/nvidia/vpi2/lib64/libnvvpi.so.* /opt/nvidia/vpi2/lib64/priv ${sysconfdir}/ld.so.conf.d"
FILES:${PN}-dev = "/opt/nvidia/vpi2/lib64/libnvvpi.so /opt/nvidia/vpi2/include ${datadir}/vpi2/cmake"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
