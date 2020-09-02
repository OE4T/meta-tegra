SUMMARY = "NVIDIA L4T CUDA binaries"
DESCRIPTION = "Downloads NVIDIA L4T binary-only CUDA packages for sharing with other recipes"
HOMEPAGE = "https://developer.nvidia.com/embedded/linux-tegra"
SECTION = "dev"

require cuda-binaries-common-defs.inc

WORKDIR = "${TMPDIR}/work-shared/${HOST_ARCH}/cuda-binaries-${PV}-${PR}"
SSTATE_SWSPEC = "sstate::cuda-binaries-${HOST_ARCH}::${PV}:${PR}::${SSTATE_VERSION}:"
STAMP = "${STAMPS_DIR}/work-shared/cuda-binaries-${HOST_ARCH}-${PV}-${PR}"
STAMPCLEAN = "${STAMPS_DIR}/work-shared/cuda-binaries-${HOST_ARCH}-${PV}-*"

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

do_preconfigure() {
    CUDA_VERSION_DASHED=$(echo ${CUDA_VERSION} | sed s/\\./-/g)
    dpkg-deb --extract ${S}/var/cuda-repo-${CUDA_VERSION_DASHED}-local${CUDA_REPO_EXTRA}/cuda-license-${CUDA_VERSION_DASHED}_${PV}_${CUDA_DEB_PKGARCH}.deb ${S}
}
do_preconfigure[depends] += "dpkg-native:do_populate_sysroot"
addtask preconfigure after do_patch
do_populate_lic[depends] += "${PN}:do_preconfigure"
