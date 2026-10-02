SUMMARY = "NVIDIA Vulkan SC driver discovery manifest"
HOMEPAGE = "https://developer.nvidia.com/embedded/jetpack"
LICENSE = "LicenseRef-Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/nvidia-l4t-vulkan-sc-sdk/copyright;md5=fea1f6ae9b5b80f1b769ae7c6cd85882"

COMPATIBLE_MACHINE = "(tegra)"

inherit l4t_deb_pkgfeed features_check

REQUIRED_DISTRO_FEATURES = "vulkan"

SRC_SOC_DEBS = "${@l4t_deb_pkgname(d, 'vulkan-sc-sdk')};subdir=${BP};name=main"
SRC_URI[main.sha256sum] = "874c32a600485cd4b14cfac2379907630fc6cac110f695e230384132833d899c"

S = "${UNPACKDIR}/${BP}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sysconfdir}/vulkansc/icd.d
    install -m 0644 ${S}/etc/vulkansc/icd.d/nvidia_icd_vksc.json ${D}${sysconfdir}/vulkansc/icd.d/
}

FILES:${PN} = "${sysconfdir}/vulkansc/icd.d/nvidia_icd_vksc.json"
RDEPENDS:${PN} = "tegra-libraries-vulkan-sc-core"
PACKAGE_ARCH = "${L4T_BSP_PKGARCH}"
