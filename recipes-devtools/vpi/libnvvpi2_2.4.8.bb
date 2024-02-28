SUMMARY = "Vision Programming Interface(VPI) is an API for accelerated \
  computer vision and image processing for embedded systems."
HOMEPAGE = "https://developer.nvidia.com/embedded/vpi"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = " \
    file://opt/nvidia/vpi2/include/vpi/VPI.h;endline=48;md5=7c9511c3e53f3d844d189edd66c44682 \
    file://opt/nvidia/vpi2/doc/VPI_EULA.txt;md5=a8a314954f2495dabebb8a9ccc2247ae"

inherit l4t_deb_pkgfeed features_check

SRC_COMMON_DEBS = "\
    libnvvpi2_${PV}_arm64.deb;name=lib;subdir=vpi2 \
    vpi2-dev_${PV}_arm64.deb;name=dev;subdir=vpi2 \
"
L4T_DEB_GROUP[dev] = "vpi2-dev"
SRC_URI[lib.sha256sum] = "9731fb1aca401455831c6d4703d48c530e6463d3a53e39377f14ee2f75aa3819"
SRC_URI[dev.sha256sum] = "d79d44eab50eeee7ab666b63a6d9e85fa5e12ef36e5d50cc0c632d7b2088fc95"

REQUIRED_DISTRO_FEATURES = "opengl"

S = "${WORKDIR}/vpi2"
B = "${S}"

DEPENDS = "cuda-cudart libcufft tegra-libraries-multimedia-utils tegra-libraries-multimedia tegra-libraries-eglcore \
           tegra-libraries-pva tegra-libraries-nvsci tegra-libraries-cuda libnpp cupva"
SYSROOT_DIRS:append = " /opt"

COMPATIBLE_MACHINE = "(tegra)"

do_compile() {
    :
}

do_install() {
    install -d ${D}/opt/nvidia/vpi2
    cp -R --preserve=mode,timestamps ${B}/opt/nvidia/vpi2/include ${D}/opt/nvidia/vpi2/
    cp -R --preserve=mode,timestamps ${B}/opt/nvidia/vpi2/lib ${D}/opt/nvidia/vpi2/
    ln -snf lib/aarch64-linux-gnu ${D}/opt/nvidia/vpi2/lib64
    install -d ${D}${sysconfdir}/ld.so.conf.d
    install -m 0644 ${B}/opt/nvidia/vpi2/etc/ld.so.conf.d/vpi2.conf ${D}${sysconfdir}/ld.so.conf.d/
    install -d ${D}${nonarch_base_libdir}/firmware
    install -m 0644 ${B}/opt/nvidia/vpi2/lib/aarch64-linux-gnu/priv/vpi2_pva_auth_allowlist ${D}${nonarch_base_libdir}/firmware/pva_auth_allowlist
    rm -f ${D}/opt/nvidia/vpi2/lib/aarch64-linux-gnu/priv/vpi2_pva_auth_allowlist
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

PACKAGES = "${PN} ${PN}-dev"
FILES:${PN} = "/opt/nvidia/vpi2/lib/aarch64-linux-gnu/lib*${SOLIBS} /opt/nvidia/vpi2/lib/aarch64-linux-gnu/priv /opt/nvidia/vpi2/lib64 ${sysconfdir}/ld.so.conf.d ${nonarch_base_libdir}/firmware"
FILES:${PN}-dev = "/opt/nvidia/vpi2/lib/aarch64-linux-gnu/lib*${SOLIBSDEV} /opt/nvidia/vpi2/include /opt/nvidia/vpi2/lib/aarch64-linux-gnu/cmake"
RDEPENDS:${PN} += "tegra-libraries-nvsci tegra-libraries-cuda libcufft"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
