COMPATIBLE_MACHINE_class-target = "(cuda)"
CUDA_VERSION ??= "10.0"
CUDA_REPO_EXTRA = "-10.0.326"
CUDA_REPO_EXTRA_x86-64 = "-10.0.326-410.108"

S = "${TMPDIR}/work-shared/${HOST_ARCH}/cuda-binaries-${PV}-${PR}"
B = "${WORKDIR}/build"
PR = "r1"

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
