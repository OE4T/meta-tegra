L4T_DEB_COPYRIGHT_MD5 = "ce4d36df31e6cc73581fd2a25d16834e"
DEPENDS = "tegra-libraries-core tegra-libraries-glxcore"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

MAINSUM = "c6b3b72a3abbc8f88013d4447e9570465573f2f85ee79b2f1d7cdfc2ccbf669d"
MAINSUM_tegra210 = "96f7eaa8832fbe68d585e5dfed9b1c10db738e53289e8a06c6b4bd5535ea120f"

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
