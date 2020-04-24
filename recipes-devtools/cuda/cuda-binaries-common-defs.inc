COMPATIBLE_MACHINE_class-target = "(cuda)"
CUDA_VERSION ??= "10.2"
CUDA_REPO_EXTRA = "-10.2.89"
CUDA_REPO_EXTRA_x86-64 = "-10.2.89-440.40"
CUDA_FULL_VERSION ?= "${PV}"

S = "${WORKDIR}/${BP}"
B = "${S}"

S_x86-64 = "${TMPDIR}/work-shared/${HOST_ARCH}/cuda-binaries-${CUDA_FULL_VERSION}-${PR}"
B_x86-64 = "${WORKDIR}/build"

PR = "r0"

CUDA_DEB_PKGARCH = "${@cuda_deb_pkg_arch(d)}"

def cuda_deb_pkg_arch(d):
    arch = d.getVar('HOST_ARCH')
    if arch == 'x86_64':
        return 'amd64'
    elif arch == 'aarch64':
        return 'arm64'
    bb.error("unrecognized HOST_ARCH: %s" % arch)
    return 'UNKNOWN'

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
