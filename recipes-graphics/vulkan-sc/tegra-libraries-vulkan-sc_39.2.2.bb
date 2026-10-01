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
SRC_URI[sdk.sha256sum] = "e978e0f7f18c22e48a89522b68ab373f0587041118104f3c65b7fe58f2689af8"
SRC_URI[dev.sha256sum] = "0c816e9608eb64f2098d3bd1538b2e42b0f24d8c52d8cab53ff920f27ba28153"

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
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/nvidia/libVkSCLayer_khronos_validation.so ${D}${libdir}/

    install -d ${D}${sysconfdir}/vulkansc/icd.d
    install -m 0644 ${S}/etc/vulkansc/icd.d/*.json ${D}${sysconfdir}/vulkansc/icd.d/
    rm -f ${D}${sysconfdir}/vulkansc/icd.d/VkLayer_json_gen.json

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
    ${libdir}/libVkSCLayer_khronos_validation.so \
    ${sysconfdir}/vulkansc/ \
    ${bindir}/pcc \
"
FILES:${PN}-dev = "\
    ${libdir}/libvulkansc.so \
    ${includedir}/VulkanSC/ \
"

INSANE_SKIP = "dev-so ldflags"
RDEPENDS:${PN} += "tegra-libraries-vulkan-sc-core"
