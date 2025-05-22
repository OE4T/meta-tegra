DESCRIPTION = "Tegra-specific firmware from the L4T BSP"
L4T_DEB_COPYRIGHT_MD5 = "90328d200c192774c9f168bf103b193e"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-firmware"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'xusb-firmware')};subdir=${BP};name=xusb"
MAINSUM = "ec89d6a5c449bc6e7e89127bc7390bea7a113a69f92a4dc15462e9e943fad3ab"
XUSBSUM = "411a41513403cc306a07aaa7e27453398e857c8f7fe0f746086202f4c7c3ff62"
SRC_URI[xusb.sha256sum] = "${XUSBSUM}"

do_install() {
    install -d ${D}${nonarch_base_libdir}
    cp -R -f ${S}/lib/firmware ${D}${nonarch_base_libdir}
}

PACKAGES = "${PN}-rtl8822 ${PN}-brcm ${PN}-tegra234 ${PN}-xusb ${PN}-vic ${PN}"
FILES:${PN}-brcm = "${nonarch_base_libdir}/firmware/brcm ${nonarch_base_libdir}/firmware/bcm4354.hcd ${nonarch_base_libdir}/firmware/nv-*-Version \
                    ${nonarch_base_libdir}/firmware/cypress ${nonarch_base_libdir}/firmware/bcm4359.hcd"
FILES:${PN}-rtl8822 = "${nonarch_base_libdir}/firmware/rtl8822*"
FILES:${PN}-tegra234 = " \
    ${nonarch_base_libdir}/firmware/tegra23x  \
    ${nonarch_base_libdir}/firmware/nvidia/tegra234 \
    ${nonarch_base_libdir}/firmware/nvidia/ga10b \
    ${nonarch_base_libdir}/firmware/nvpva_020.fw \
    ${nonarch_base_libdir}/firmware/dce.bin \
    ${nonarch_base_libdir}/firmware/nvpva_010.fw \
    ${nonarch_base_libdir}/firmware/nvhost_nvdla020.fw \
    ${nonarch_base_libdir}/firmware/nvhost_nvdla030.fw \
    ${nonarch_base_libdir}/firmware/nvhost_nvdla010.fw \
    ${nonarch_base_libdir}/firmware/nvhost_ofa012.fw \
    ${nonarch_base_libdir}/firmware/display-t234-dce.bin \
    ${nonarch_base_libdir}/firmware/nvidia/gv11b \
    ${nonarch_base_libdir}/firmware/nvidia/tegra194 \
    ${nonarch_base_libdir}/firmware/tegra19x_xusb_firmware \
    ${nonarch_base_libdir}/firmware/tegra19x \
"
FILES:${PN}-xusb = ""
ALLOW_EMPTY:${PN}-xusb = "1"
FILES:${PN}-vic = "${nonarch_base_libdir}/firmware/nvhost_vic042.fw"
FILES:${PN} = ""
ALLOW_EMPTY:${PN} = "1"
XUSBDEPS = ""
RDEPENDS:${PN}-xusb = "${XUSBDEPS}"
FWDEPS = ""
FWDEPS:tegra234 = "${PN}-tegra234 ${PN}-vic"
RDEPENDS:${PN} = "${FWDEPS} ${PN}-xusb"
RPROVIDES:${PN}:tegra = "linux-firmware-nvidia-tegra"
RREPLACES:${PN}:tegra = "linux-firmware-nvidia-tegra"
RCONFLICTS:${PN}:tegra = "linux-firmware-nvidia-tegra"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

