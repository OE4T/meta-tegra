COMPATIBLE_MACHINE:class-target = "(cuda)"
COMPATIBLE_HOST = "(x86_64|aarch64)"
CUDA_VERSION ??= "13.0"

S = "${UNPACKDIR}/${BP}"
B = "${S}"

PR = "r0"

CUDA_DEB_PKGARCH = "${@cuda_deb_pkg_arch(d)}"

def cuda_deb_pkg_arch(d):
    arch = d.getVar('HOST_ARCH')
    if arch == 'x86_64':
        return 'amd64'
    elif arch == 'aarch64':
        return 'arm64'
    bb.warn("unrecognized HOST_ARCH %s, defaulting to arm64" % arch)
    return 'arm64'

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
