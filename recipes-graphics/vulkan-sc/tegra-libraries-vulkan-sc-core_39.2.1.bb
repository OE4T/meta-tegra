SUMMARY = "NVIDIA Vulkan Safety Critical core driver library"
HOMEPAGE = "https://developer.nvidia.com/embedded/jetpack"
LICENSE = "LicenseRef-Proprietary"
LIC_FILES_CHKSUM:tegra234 = "file://usr/share/doc/nvidia-l4t-vulkan-sc-nvgpu/copyright;md5=2efa2b52d37f190602d403961e089024"
LIC_FILES_CHKSUM:tegra264 = "file://usr/share/doc/nvidia-l4t-vulkan-sc-openrm/copyright;md5=bf057ebea50d20e3884056bebc851ce6"

COMPATIBLE_MACHINE = "(tegra234|tegra264)"

inherit l4t_deb_pkgfeed features_check

REQUIRED_DISTRO_FEATURES = "vulkan"

MAINSUM:tegra234 = "ab64c4b940ebf9af164964c2e32fb18ebeaff7623557f66a7798d85e2ce3a7d6"
MAINSUM:tegra264 = "0b9877ad0b7ad7a6cc1bc20a200c335e03e26cd69028b5b6ea3d2a3f38fe6368"

SRC_SOC_DEBS:tegra234 = "${@l4t_deb_pkgname(d, 'vulkan-sc-nvgpu')};subdir=${BP};name=main"
SRC_SOC_DEBS:tegra264 = "${@l4t_deb_pkgname(d, 'vulkan-sc-openrm')};subdir=${BP};name=main"

SRC_URI[main.sha256sum] = "${MAINSUM}"

S = "${UNPACKDIR}/${BP}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install:tegra234() {
    install -d ${D}${libdir}
    install -m 0644 ${S}/opt/nvidia/l4t-gpu-libs/nvgpu/libnvidia-vksc-core.so.${L4T_LIB_VERSION} ${D}${libdir}/
    ln -sf libnvidia-vksc-core.so.${L4T_LIB_VERSION} ${D}${libdir}/libnvidia-vksc-core.so.1
    ln -sf libnvidia-vksc-core.so.1 ${D}${libdir}/libnvidia-vksc-core.so
}

do_install:tegra264() {
    install -d ${D}${libdir}
    install -m 0644 ${S}/opt/nvidia/l4t-gpu-libs/openrm/libnvidia-vksc-openrm.so.${L4T_LIB_VERSION} ${D}${libdir}/
    ln -sf libnvidia-vksc-openrm.so.${L4T_LIB_VERSION} ${D}${libdir}/libnvidia-vksc-openrm.so.1
    ln -sf libnvidia-vksc-openrm.so.1 ${D}${libdir}/libnvidia-vksc-openrm.so
    ln -sf libnvidia-vksc-openrm.so.${L4T_LIB_VERSION} ${D}${libdir}/libnvidia-vksc-core.so.1
    ln -sf libnvidia-vksc-core.so.1 ${D}${libdir}/libnvidia-vksc-core.so
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP = "dev-so ldflags"
RDEPENDS:${PN} += "tegra-libraries-core tegra-libraries-eglcore tegra-libraries-nvsci"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
