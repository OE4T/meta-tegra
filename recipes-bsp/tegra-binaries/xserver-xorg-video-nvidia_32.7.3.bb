L4T_DEB_COPYRIGHT_MD5 = "ce4d36df31e6cc73581fd2a25d16834e"
DEPENDS = "tegra-libraries-core tegra-libraries-glxcore"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "18aa04998115a2d4e3285ddd3f864cf5a5e5e6cd07c9b82f49bf63c347506fcb"
MAINSUM:tegra210 = "6928ebe44d8097622ef24e1c505fe45c69415ee2db8614ecffb11a35f5d0a343"

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
