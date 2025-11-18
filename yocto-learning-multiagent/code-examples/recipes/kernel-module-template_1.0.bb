# Kernel Module Recipe Template
# This recipe demonstrates building and installing Linux kernel modules
#
# Key concepts:
# - Inheriting module class
# - Kernel version compatibility
# - Module installation and autoloading
# - KERNEL_MODULE_AUTOLOAD and KERNEL_MODULE_PROBECONF

SUMMARY = "Example Kernel Module Template"
DESCRIPTION = "Template recipe for building out-of-tree kernel modules for Jetson platforms. \
               Demonstrates module building, installation, and automatic loading configuration."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=placeholder_md5"

SECTION = "kernel/modules"

PV = "1.0"
PR = "r0"

# Source files
SRC_URI = "file://kernel_module.c \
           file://Makefile \
           file://LICENSE \
          "

S = "${WORKDIR}"

# Inherit module class - this provides kernel module building infrastructure
# The module class sets up proper kernel headers, cross-compilation, and installation
inherit module

# Kernel module dependencies
# DEPENDS on virtual/kernel ensures kernel is built before this module
DEPENDS = "virtual/kernel"

# Runtime dependencies
# kern-tools-native might be needed for specific kernel operations
DEPENDS:append = " kern-tools-native"

# Ensure module is rebuilt when kernel changes
do_compile[depends] += "virtual/kernel:do_shared_workdir"

# Module name (used for configuration)
MODULE_NAME = "kernel_module"

# Kernel version compatibility check (optional but recommended)
# This ensures the module is compatible with the target kernel version
python do_check_kernel_version() {
    import re
    kernel_version = d.getVar('KERNEL_VERSION', True)
    min_kernel = "4.9"

    if kernel_version and kernel_version < min_kernel:
        bb.warn("This module requires kernel >= %s, current is %s" % (min_kernel, kernel_version))
}
addtask check_kernel_version before do_compile after do_fetch

# Build configuration
EXTRA_OEMAKE += "\
    KERNEL_SRC=${STAGING_KERNEL_DIR} \
    KERNEL_VERSION=${KERNEL_VERSION} \
    CC='${KERNEL_CC}' \
    LD='${KERNEL_LD}' \
    AR='${KERNEL_AR}' \
    O=${STAGING_KERNEL_BUILDDIR} \
"

# The module class provides default do_compile, but we can override if needed
# do_compile() {
#     unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
#     oe_runmake KERNEL_PATH=${STAGING_KERNEL_DIR} \
#                KERNEL_VERSION=${KERNEL_VERSION} \
#                CC="${KERNEL_CC}" \
#                LD="${KERNEL_LD}" \
#                AR="${KERNEL_AR}" \
#                modules
# }

# The module class provides default do_install, but here's a custom example
do_install() {
    # Install kernel module
    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra
    install -m 0644 ${B}/${MODULE_NAME}.ko ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/

    # Install modprobe configuration (optional)
    # install -d ${D}${sysconfdir}/modprobe.d
    # install -m 0644 ${WORKDIR}/${MODULE_NAME}.conf ${D}${sysconfdir}/modprobe.d/

    # Install udev rules (optional, for device node creation)
    # install -d ${D}${sysconfdir}/udev/rules.d
    # install -m 0644 ${WORKDIR}/99-${MODULE_NAME}.rules ${D}${sysconfdir}/udev/rules.d/
}

# Automatically load module at boot
# KERNEL_MODULE_AUTOLOAD: Modules to load automatically
KERNEL_MODULE_AUTOLOAD += "${MODULE_NAME}"

# Module parameters (optional)
# KERNEL_MODULE_PROBECONF: Module loading parameters
# Format: "module_name parameter=value"
# Example: KERNEL_MODULE_PROBECONF += "kernel_module debug=1"
# KERNEL_MODULE_PROBECONF += "${MODULE_NAME}"

# Files to include in the package
FILES:${PN} += "\
    ${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/${MODULE_NAME}.ko \
    ${sysconfdir}/modprobe.d/* \
    ${sysconfdir}/udev/rules.d/* \
"

# Package architecture - kernel modules are machine-specific
PACKAGE_ARCH = "${MACHINE_ARCH}"

# Prevent stripping of kernel modules (they need special handling)
INHIBIT_PACKAGE_STRIP = "1"

# Generate module dependencies
pkg_postinst:${PN}() {
    #!/bin/sh
    if [ -z "$D" ]; then
        # Running on target system
        depmod -a ${KERNEL_VERSION}
    else
        # Running in build environment
        # Module dependencies will be generated during image creation
        :
    fi
}

pkg_postrm:${PN}() {
    #!/bin/sh
    if [ -z "$D" ]; then
        # Running on target system
        depmod -a ${KERNEL_VERSION}
    fi
}

# Development notes:
# 1. Module source Makefile should use:
#    obj-m := kernel_module.o
#
# 2. Build module: bitbake kernel-module-template
#
# 3. Clean build: bitbake kernel-module-template -c cleansstate
#
# 4. Test module on target:
#    modprobe kernel_module
#    lsmod | grep kernel_module
#    modinfo kernel_module
#    rmmod kernel_module
#
# 5. Include in image: IMAGE_INSTALL:append = " kernel-module-template"
#
# 6. Check module installation:
#    oe-pkgdata-util list-pkg-files kernel-module-template
#
# 7. Debug build issues:
#    bitbake kernel-module-template -c devshell
#    make V=1 modules
#
# 8. Kernel headers location: ${STAGING_KERNEL_DIR}
#
# 9. For multiple modules in one recipe, create separate .bb files
#    or use KERNEL_MODULE_AUTOLOAD with multiple module names
