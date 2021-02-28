SUMMARY = "NVIDIA L4T binaries"
DESCRIPTION = "Downloads NVIDIA L4T binary-only packages for sharing with other recipes"
SECTION = "base"

require tegra-binaries-${PV}.inc

WORKDIR = "${TMPDIR}/work-shared/L4T-${L4T_BSP_ARCH}-${PV}-${PR}"
SSTATE_SWSPEC = "sstate:tegra-binaries::${PV}:${PR}::${SSTATE_VERSION}:"
STAMP = "${STAMPS_DIR}/work-shared/L4T-${L4T_BSP_ARCH}-${PV}-${PR}"
STAMPCLEAN = "${STAMPS_DIR}/work-shared/L4T-${L4T_BSP_ARCH}-${PV}-*"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = ""
PACKAGES = ""

deltask do_configure
deltask do_compile
deltask do_package
deltask do_package_write_rpm
deltask do_package_write_ipk
deltask do_package_write_deb
deltask do_install
deltask do_populate_sysroot
deltask do_package_qa
deltask do_packagedata
RM_WORK_EXCLUDE += "${PN}"

addtask preconfigure after do_patch
