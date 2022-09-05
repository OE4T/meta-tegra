L4T_DEB_COPYRIGHT_MD5 = "ce4d36df31e6cc73581fd2a25d16834e"
DEPENDS = "tegra-libraries-core tegra-libraries-glxcore"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

<<<<<<<< HEAD:recipes-bsp/tegra-binaries/xserver-xorg-video-nvidia_32.7.2.bb
MAINSUM = "97fd11faa79ab16f5525492628108a68a58a5c1c5127ba58509784d58b90729c"
MAINSUM:tegra210 = "29fa00ca54f7776503a608989ae7baa2459cc1a1d52e075819a0a07761192c25"
========
MAINSUM = "c873bf1c0605cb98e18ebd5a0d724bdf8a4fbb21b9af5fd0b862e4a44f091e2a"
>>>>>>>> 390a51f3 (tegra-binaries: update for 34.1.0):recipes-bsp/tegra-binaries/xserver-xorg-video-nvidia_34.1.0.bb

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
