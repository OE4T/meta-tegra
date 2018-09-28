SUMMARY = "NVIDIA L4T CUDA cross-development binaries"
DESCRIPTION = "Downloads NVIDIA L4T binary-only CUDA native packages for sharing with other recipes"
SECTION = "dev"

S = "${TMPDIR}/work-shared/cuda-binaries-native-${PV}-${PR}"
B = "${WORKDIR}/build"

inherit native

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

WORKDIR = "${TMPDIR}/work-shared/cuda-binaries-native-${PV}-${PR}"
SSTATE_SWSPEC = "sstate::cuda-binaries-native::${PV}:${PR}::${SSTATE_VERSION}:"
STAMP = "${STAMPS_DIR}/work-shared/cuda-binaries-native-${PV}-${PR}"
STAMPCLEAN = "${STAMPS_DIR}/work-shared/cuda-binaries-native-${PV}-*"

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
deltask do_rm_work

CUDA_REPO_EXTRA ??= ""

do_preconfigure() {
    CUDA_VERSION_DASHED=$(echo ${CUDA_VERSION} | sed s/\\./-/g)
    dpkg-deb --extract ${S}/var/cuda-repo-${CUDA_VERSION_DASHED}-local${CUDA_REPO_EXTRA}/cuda-license-${CUDA_VERSION_DASHED}_${PV}_amd64.deb ${S}
}
do_preconfigure[depends] += "dpkg-native:do_populate_sysroot"
addtask preconfigure after do_patch
do_populate_lic[depends] += "${PN}:do_preconfigure"
