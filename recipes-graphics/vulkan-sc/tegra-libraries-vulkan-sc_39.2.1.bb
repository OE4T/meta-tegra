SUMMARY = "NVIDIA Vulkan Safety Critical loader, validation layers, and dev headers"
HOMEPAGE = "https://developer.nvidia.com/embedded/jetpack"
LICENSE = "LicenseRef-Proprietary"
LIC_FILES_CHKSUM = "\
    file://usr/share/doc/nvidia-l4t-vulkan-sc-sdk/copyright;md5=fea1f6ae9b5b80f1b769ae7c6cd85882 \
    file://usr/share/doc/nvidia-l4t-vulkan-sc-dev/copyright;md5=7b227641d976dcc6d473fb345610ac57 \
"

COMPATIBLE_MACHINE = "(tegra)"

inherit l4t_deb_pkgfeed features_check

REQUIRED_DISTRO_FEATURES = "vulkan"

# Both debs merge into the same subdir so S gives access to all contents.
SRC_SOC_DEBS = "\
    ${@l4t_deb_pkgname(d, 'vulkan-sc-sdk')};subdir=${BP};name=sdk \
    ${@l4t_deb_pkgname(d, 'vulkan-sc-dev')};subdir=${BP};name=dev \
"
SRC_URI[sdk.sha256sum] = "283c60a286746a0a29948afb62cc0db24b77aaa1eec80d7851c8cf31274e66f6"
SRC_URI[dev.sha256sum] = "9de146ce1b149306ed20ebb7bdf6a42f65a1bdcaf3e6ae7c44b46684d16065f7"

S = "${UNPACKDIR}/${BP}"

VKSC_SDK_VERSION = "1.0.10"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
PACKAGE_ARCH = "${L4T_BSP_PKGARCH}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/nvidia/libvulkansc.so.${VKSC_SDK_VERSION} ${D}${libdir}/
    ln -sf libvulkansc.so.${VKSC_SDK_VERSION} ${D}${libdir}/libvulkansc.so.1
    ln -sf libvulkansc.so.1 ${D}${libdir}/libvulkansc.so
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/nvidia/libVkLayer_json_gen.so ${D}${libdir}/
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/nvidia/libVkSCLayer_khronos_validation.so ${D}${libdir}/

    install -d ${D}${sysconfdir}/vulkansc/icd.d
    install -m 0644 ${S}/etc/vulkansc/icd.d/*.json ${D}${sysconfdir}/vulkansc/icd.d/

    # Headers from the dev deb, needed for cross-compile sysroot population
    install -d ${D}${includedir}/VulkanSC/vulkan
    install -m 0644 ${S}/usr/include/VulkanSC/vulkan/* ${D}${includedir}/VulkanSC/vulkan/

    install -d ${D}${bindir}
    install -m 0755 ${S}/usr/bin/pcc ${D}${bindir}/
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"

FILES:${PN} = "\
    ${libdir}/libvulkansc.so.* \
    ${libdir}/libVkLayer_json_gen.so \
    ${libdir}/libVkSCLayer_khronos_validation.so \
    ${sysconfdir}/vulkansc/ \
    ${bindir}/pcc \
"
FILES:${PN}-dev = "\
    ${libdir}/libvulkansc.so \
    ${includedir}/VulkanSC/ \
"

INSANE_SKIP = "dev-so ldflags"
RDEPENDS:${PN} += "libssl libcrypto tegra-libraries-vulkan-sc-core"
