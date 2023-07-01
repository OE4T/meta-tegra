L4T_DEB_COPYRIGHT_MD5 = "ce4d36df31e6cc73581fd2a25d16834e"
DEPENDS = "tegra-libraries-core tegra-libraries-glxcore"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "d65d21276997377e5fb744e25439d19c264cf1172c5a223a94a45e0d9964af79"
MAINSUM_tegra210 = "6ff6d996a737f62230cc3b55f63a737f30ca1daf68dc52cecd1247d92341d95d"

do_install() {
    install -d ${D}${libdir}/xorg/modules/drivers
    install -m 0644 ${S}/usr/lib/xorg/modules/drivers/nvidia_drv.so ${D}${libdir}/xorg/modules/drivers/
    install -d ${D}${libdir}/xorg/modules/extensions
    install -m 0644 ${S}/usr/lib/xorg/modules/extensions/libglxserver_nvidia.so ${D}${libdir}/xorg/modules/extensions/
}

PACKAGES = "${PN}"
FILES_${PN} = "${libdir}"
RDEPENDS_${PN} += "tegra-configs-xorg"
RPROVIDES_${PN} += "xserver-xorg-extension-glx"
RCONFLICTS_${PN} = "xserver-xorg-extension-glx"

INSANE_SKIP_${PN} = "dev-so textrel ldflags xorg-driver-abi"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
