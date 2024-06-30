L4T_DEB_COPYRIGHT_MD5 = "03753bf7be89a121c8d3fd11c4267db9"
DEPENDS = "tegra-libraries-core tegra-libraries-glxcore"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "31b5c0de9fc673a540e6c331ae18cb981fc6de1145f4be76419bed0f9de1959b"
MAINSUM:tegra210 = "13fa8733d146999ac1a6e2c37c70c2f4b64988c5bcd5a664f2fda4bb4a04680b"

do_install() {
    install -d ${D}${libdir}/xorg/modules/drivers
    install -m 0644 ${S}/usr/lib/xorg/modules/drivers/nvidia_drv.so ${D}${libdir}/xorg/modules/drivers/
    install -d ${D}${libdir}/xorg/modules/extensions
    install -m 0644 ${S}/usr/lib/xorg/modules/extensions/libglxserver_nvidia.so ${D}${libdir}/xorg/modules/extensions/
}

PACKAGES = "${PN}"
FILES:${PN} = "${libdir}"
RDEPENDS:${PN} += "tegra-configs-xorg"
RPROVIDES:${PN} += "xserver-xorg-extension-glx"
RCONFLICTS:${PN} = "xserver-xorg-extension-glx"

INSANE_SKIP:${PN} = "dev-so textrel ldflags xorg-driver-abi"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
