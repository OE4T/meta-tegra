SUMMARY = "Vision Programming Interface(VPI) is an API for accelerated \
  computer vision and image processing for embedded systems."
HOMEPAGE = "https://developer.nvidia.com/embedded/vpi"
LICENSE = "LicenseRef-Proprietary"
LIC_FILES_CHKSUM = " \
    file://opt/nvidia/vpi4/doc/LICENSE.Boost;md5=5babd6e46e208e82f5759113623a6059 \
    file://opt/nvidia/vpi4/doc/LICENSE.Ceres;md5=b25c1d0dcdc903abbcc42fc0d3c2bd7a \
    file://opt/nvidia/vpi4/doc/LICENSE.OpenCV;md5=53f5028e157919e3649269b83e49714b \
    file://opt/nvidia/vpi4/doc/LICENSE.apriltag;md5=f1cc3c108635a431d970ef1ed62b4a02 \
    file://opt/nvidia/vpi4/doc/LICENSE.eigen;md5=ef3527b5a9477f44b12eba5ab85f9602 \
    file://opt/nvidia/vpi4/doc/LICENSE.expected;md5=e5d9538b3f666dfe10e32272f2c1f426 \
    file://opt/nvidia/vpi4/doc/LICENSE.fltk;md5=be54a79c2f53136d492e4a60c42bec0a \
    file://opt/nvidia/vpi4/doc/LICENSE.for_constexpr;md5=6c9eec8e0e6d236ecf1c46b913f91e71 \
    file://opt/nvidia/vpi4/doc/LICENSE.kissfft;md5=dfcf4ada1ee274b299ddfa3184102419 \
    file://opt/nvidia/vpi4/doc/LICENSE.libSGM;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://opt/nvidia/vpi4/doc/LICENSE.newlib;md5=a3eb2bde2affa6734922f83bce5993b3 \
    file://opt/nvidia/vpi4/doc/LICENSE.pybind11;md5=774f65abd8a7fe3124be2cdf766cd06f \
    file://opt/nvidia/vpi4/doc/LICENSE.softfloat;md5=407449d347fc06e16ed733726186c794 \
    file://opt/nvidia/vpi4/doc/VPI_EULA.txt;md5=a8a314954f2495dabebb8a9ccc2247ae \
"

inherit l4t_deb_pkgfeed features_check

SRC_COMMON_DEBS = "\
    libnvvpi4_${PV}_arm64.deb;name=lib;subdir=vpi4 \
    vpi4-dev_${PV}_arm64.deb;name=dev;subdir=vpi4 \
"
L4T_DEB_GROUP[dev] = "vpi4-dev"
SRC_URI[lib.sha256sum] = "07fc2bab8816be3258592e7f2eb4f5613e56cae14968bc95123924e0c253c6ac"
SRC_URI[dev.sha256sum] = "d1956c12300daf24e93f1558b99f39b89d4866c1cc771fb5cccbafb889eb9a30"

REQUIRED_DISTRO_FEATURES = "opengl"

S = "${UNPACKDIR}/vpi4"
B = "${S}"

DEPENDS = "cuda-cudart libcufft tegra-libraries-multimedia-utils tegra-libraries-multimedia tegra-libraries-eglcore \
           tegra-libraries-pva tegra-libraries-nvsci tegra-libraries-cuda libnpp"
DEPENDS:append:tegra264 = " tegra-libraries-video-codec"
SYSROOT_DIRS:append = " /opt"

COMPATIBLE_MACHINE = "(tegra)"

do_compile() {
    :
}

do_install() {
    install -d ${D}/opt/nvidia/vpi4
    cp -R --preserve=mode,timestamps ${B}/opt/nvidia/vpi4/include ${D}/opt/nvidia/vpi4/
    cp -R --preserve=mode,timestamps ${B}/opt/nvidia/vpi4/lib ${D}/opt/nvidia/vpi4/
    ln -snf lib/aarch64-linux-gnu ${D}/opt/nvidia/vpi4/lib64
    install -d ${D}${sysconfdir}/ld.so.conf.d
    install -m 0644 ${B}/opt/nvidia/vpi4/etc/ld.so.conf.d/vpi4.conf ${D}${sysconfdir}/ld.so.conf.d/
    install -d ${D}${nonarch_base_libdir}/firmware
    install -m 0644 ${B}/opt/nvidia/vpi4/lib/aarch64-linux-gnu/priv/vpi4_pva_auth_allowlist ${D}${nonarch_base_libdir}/firmware/pva_auth_allowlist
    rm -f ${D}/opt/nvidia/vpi4/lib/aarch64-linux-gnu/priv/vpi4_pva_auth_allowlist
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

PACKAGES = "${PN} ${PN}-dev"
FILES:${PN} = "/opt/nvidia/vpi4/lib/aarch64-linux-gnu/lib*${SOLIBS} /opt/nvidia/vpi4/lib/aarch64-linux-gnu/priv /opt/nvidia/vpi4/lib64 ${sysconfdir}/ld.so.conf.d ${nonarch_base_libdir}/firmware"
FILES:${PN}-dev = "/opt/nvidia/vpi4/lib/aarch64-linux-gnu/lib*${SOLIBSDEV} /opt/nvidia/vpi4/include /opt/nvidia/vpi4/lib/aarch64-linux-gnu/cmake"
RDEPENDS:${PN} += "tegra-libraries-nvsci tegra-libraries-cuda libcufft libnpp"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
