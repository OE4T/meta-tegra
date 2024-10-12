SUMMARY = "Vision Programming Interface(VPI) is an API for accelerated \
  computer vision and image processing for embedded systems."
HOMEPAGE = "https://developer.nvidia.com/embedded/vpi"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = " \
    file://opt/nvidia/vpi3/doc/LICENSE.fltk;md5=be54a79c2f53136d492e4a60c42bec0a \
    file://opt/nvidia/vpi3/doc/LICENSE.for_constexpr;md5=6c9eec8e0e6d236ecf1c46b913f91e71 \
    file://opt/nvidia/vpi3/doc/LICENSE.kissfft;md5=dfcf4ada1ee274b299ddfa3184102419 \
    file://opt/nvidia/vpi3/doc/LICENSE.libSGM;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://opt/nvidia/vpi3/doc/LICENSE.newlib;md5=a3eb2bde2affa6734922f83bce5993b3 \
    file://opt/nvidia/vpi3/doc/LICENSE.OpenCV;md5=637c2c054871a67de67d5a619c610cad \
    file://opt/nvidia/vpi3/doc/LICENSE.pybind11;md5=774f65abd8a7fe3124be2cdf766cd06f \
    file://opt/nvidia/vpi3/doc/LICENSE.softfloat;md5=407449d347fc06e16ed733726186c794 \
    file://opt/nvidia/vpi3/doc/VPI_EULA.txt;md5=a8a314954f2495dabebb8a9ccc2247ae \
"

inherit l4t_deb_pkgfeed features_check

SRC_COMMON_DEBS = "\
    libnvvpi3_${PV}_arm64.deb;name=lib;subdir=vpi3 \
    vpi3-dev_${PV}_arm64.deb;name=dev;subdir=vpi3 \
"
L4T_DEB_GROUP[dev] = "vpi3-dev"
SRC_URI[lib.sha256sum] = "5589eac155bba4a2f881963bb4e3c4cd06c7fc0fa12cb6d73196dca496daab1b"
SRC_URI[dev.sha256sum] = "d073a25920a35d92b69bb08e41a1b93f76050a7fb567a89aa327b9349cefd843"

REQUIRED_DISTRO_FEATURES = "opengl"

S = "${WORKDIR}/vpi3"
B = "${S}"

DEPENDS = "cuda-cudart libcufft tegra-libraries-multimedia-utils tegra-libraries-multimedia tegra-libraries-eglcore \
           tegra-libraries-pva tegra-libraries-nvsci tegra-libraries-cuda libnpp cupva"
SYSROOT_DIRS:append = " /opt"

COMPATIBLE_MACHINE = "(tegra)"

do_compile() {
    :
}

do_install() {
    install -d ${D}/opt/nvidia/vpi3
    cp -R --preserve=mode,timestamps ${B}/opt/nvidia/vpi3/include ${D}/opt/nvidia/vpi3/
    cp -R --preserve=mode,timestamps ${B}/opt/nvidia/vpi3/lib ${D}/opt/nvidia/vpi3/
    ln -snf lib/aarch64-linux-gnu ${D}/opt/nvidia/vpi3/lib64
    install -d ${D}${sysconfdir}/ld.so.conf.d
    install -m 0644 ${B}/opt/nvidia/vpi3/etc/ld.so.conf.d/vpi3.conf ${D}${sysconfdir}/ld.so.conf.d/
    install -d ${D}${nonarch_base_libdir}/firmware
    install -m 0644 ${B}/opt/nvidia/vpi3/lib/aarch64-linux-gnu/priv/vpi3_pva_auth_allowlist ${D}${nonarch_base_libdir}/firmware/pva_auth_allowlist
    rm -f ${D}/opt/nvidia/vpi3/lib/aarch64-linux-gnu/priv/vpi3_pva_auth_allowlist
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

PACKAGES = "${PN} ${PN}-dev"
FILES:${PN} = "/opt/nvidia/vpi3/lib/aarch64-linux-gnu/lib*${SOLIBS} /opt/nvidia/vpi3/lib/aarch64-linux-gnu/priv /opt/nvidia/vpi3/lib64 ${sysconfdir}/ld.so.conf.d ${nonarch_base_libdir}/firmware"
FILES:${PN}-dev = "/opt/nvidia/vpi3/lib/aarch64-linux-gnu/lib*${SOLIBSDEV} /opt/nvidia/vpi3/include /opt/nvidia/vpi3/lib/aarch64-linux-gnu/cmake"
RDEPENDS:${PN} += "tegra-libraries-nvsci tegra-libraries-cuda libcufft"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
