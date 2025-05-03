SUMMARY = "Linux kernel modules for Nvidia Display"
DESCRIPTION = "${SUMMARY}"
LICENSE = "MIT | GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1d5fa2a493e937d5a4b96e5e03b90f7c"

inherit module

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/nvidia_kernel_display_driver_source.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-35.6.1.inc

COMPATIBLE_MACHINE = "(tegra234)"

S = "${WORKDIR}/nvdisplay"
B = "${S}"

MODULES_MODULE_SYMVERS_LOCATION = "kernel-open"

# adding INHIBIT_PACKAGE_DEBUG_SPLIT as workaround to avoid issues during
# debug-splitting that's messing up the debug symbols of the .ko files.
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

EXTRA_OEMAKE += " \
    IGNORE_XEN_PRESENCE='1' \
    IGNORE_CC_MISMATCH='1' \
    NV_EXCLUDE_BUILD_MODULES='' \
    NV_VERBOSE='1' \
    TARGET_ARCH='${HOST_ARCH}' \
    SYSSRC='${STAGING_KERNEL_DIR}' \
    SYSOUT='${STAGING_KERNEL_BUILDDIR}' \
"

do_compile() {
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    oe_runmake CC="${KERNEL_CC}" LD="${KERNEL_LD}" AR="${KERNEL_AR}" modules
}

KERNEL_MODULE_PROBECONF = "nvidia"
module_conf_nvidia = 'options nvidia rm_firmware_active="all" NVreg_RegistryDwords="RMHdcpKeyglobZero=1"'

RPROVIDES:${PN} += " \
    kernel-module-nvidia \
    kernel-module-nvidia-drm \
    kernel-module-nvidia-modeset \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"
