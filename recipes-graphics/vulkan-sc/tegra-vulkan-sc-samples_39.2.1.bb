SUMMARY = "NVIDIA Vulkan Safety Critical sample applications (built from source)"
HOMEPAGE = "https://developer.nvidia.com/embedded/jetpack"
LICENSE = "LicenseRef-Proprietary"
LIC_FILES_CHKSUM = "file://license.txt;md5=dcf473723faabf17baa9b5f2207599d0"

COMPATIBLE_MACHINE = "(tegra)"

inherit l4t_deb_pkgfeed cmake features_check qemu

REQUIRED_DISTRO_FEATURES = "vulkan"

PACKAGE_WRITE_DEPS += "qemuwrapper-cross"

SRC_SOC_DEBS = "${@l4t_deb_pkgname(d, 'vulkan-sc-samples')};subdir=${BP};name=main"
SRC_URI[main.sha256sum] = "529a81d473fa846bd7032b0e30ac0c30c05dee969508c1407f4e8cbb700b600f"

SRC_URI:append = " file://0001-fix-oe-cross-compile-paths.patch"

# tegra-libraries-vulkan-sc provides: libvulkansc.so + VulkanSC headers for build
# tegra-libraries-nvsci provides: libnvscibuf.so.1 and libnvscisync.so.1
# tegra-libraries-openwfd provides: libtegrawfd.so
# vulkan-loader provides: libvulkan.so (for the non-SC vk_01tri / vk_computeparticles targets)
DEPENDS = "\
    tegra-libraries-vulkan-sc \
    tegra-libraries-nvsci \
    tegra-libraries-openwfd \
    vulkan-loader \
    libglvnd \
"

S = "${UNPACKDIR}/${BP}/usr/src/nvidia/vulkan-sc/vulkan-sc-ecosystem/vulkan-sc-sample"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/bin/vksc_01tri ${D}${bindir}/
    install -m 0755 ${B}/bin/vk_01tri ${D}${bindir}/
    install -m 0755 ${B}/bin/vksc_computeparticles ${D}${bindir}/
    install -m 0755 ${B}/bin/vk_computeparticles ${D}${bindir}/
    install -m 0755 ${B}/bin/vulkanscinfo ${D}${bindir}/

    # Pipeline cache, pre-compiled SPIR-V, and texture data referenced at runtime
    install -d ${D}${datadir}/vulkan-sc/data
    find ${B}/bin/data -mindepth 1 -type d | while read dir; do
        install -d ${D}${datadir}/vulkan-sc/data/${dir#${B}/bin/data/}
    done
    find ${B}/bin/data -type f | while read f; do
        install -m 0644 "$f" ${D}${datadir}/vulkan-sc/data/${f#${B}/bin/data/}
    done
}

FILES:${PN} = "${bindir}/ ${datadir}/vulkan-sc/"

RDEPENDS:${PN} = "\
    tegra-libraries-vulkan-sc \
    tegra-libraries-nvsci \
    tegra-libraries-openwfd \
    vulkan-loader \
    libglvnd \
"

# Pipeline caches encode GPU microarchitecture-specific binary and are compiled by pcc
# (provided by tegra-libraries-vulkan-sc) at image-creation time via qemuwrapper.
VKSC_PCC_CHIP ??= ""
VKSC_PCC_CHIP:tegra234 = "ga10b"
VKSC_PCC_CHIP:tegra264 = "gb10b"

pkg_postinst:${PN}() {
    test -z "${VKSC_PCC_CHIP}" && exit 0
    if test -n "$D"; then
        $INTERCEPT_DIR/postinst_intercept compile_vksc_pipeline_cache ${PKG} mlprefix=${MLPREFIX} \
            chip=${VKSC_PCC_CHIP} datadir=${datadir} bindir=${bindir}
    else
        for pipeline_dir in "${datadir}"/vulkan-sc/data/pipeline/*/; do
            [ -d "$pipeline_dir" ] || continue
            pcc -chip "${VKSC_PCC_CHIP}" \
                -path "$pipeline_dir" \
                -out "${pipeline_dir}pipeline_cache.bin"
        done
    fi
}
