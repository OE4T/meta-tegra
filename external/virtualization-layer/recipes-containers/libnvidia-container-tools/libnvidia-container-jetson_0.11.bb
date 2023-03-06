SUMMARY = "NVIDIA container runtime library for Jetson platforms"

DESCRIPTION = "NVIDIA container runtime library \
The nvidia-container library provides an interface to configure GNU/Linux \
containers leveraging NVIDIA hardware. The implementation relies on several \
kernel subsystems and is designed to be agnostic of the container runtime. \
This recipe builds a Jetson-specific version of the library. \
"
HOMEPAGE = "https://github.com/NVIDIA/libnvidia-container"

COMPATIBLE_MACHINE = "(tegra)"

DEPENDS = " \
    coreutils-native \
    pkgconfig-native \
    libcap \
    elfutils \
    libtirpc126 \
    ldconfig-native \
"
LICENSE = "BSD-3-Clause & MIT & Proprietary"

# Both source repositories include GPL COPYING (and for
# libnvidia-container, COPYING.LESSER) files. However:
# * For libnvidia-container, those files might only apply if elfutils
#   sources were included (the makefile has commands to download and
#   build libelf from elfutils sources). We configure the build to
#   use libelf provided externally.
# * For nvidia-modprobe, only the nvidia-modprobe-utils library is
#   built and used.  All sources for that library are MIT-licensed.

LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=06cff45c51018e430083a716510821b7 \
    file://deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION}/modprobe-utils/nvidia-modprobe-utils.c;endline=22;md5=8f11a22ea12c5aecde3340212f7fc9a1 \
    file://deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION}/modprobe-utils/pci-enum.h;endline=29;md5=b2c0e63b1fa594dcb4f4093247d74a29 \
    file://deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION}/modprobe-utils/pci-sysfs.c;endline=25;md5=a5eee0d4ba40238ac5823a33ead29b6d \
    file://src/cuda.h;endline=48;md5=d212e7eced2562852ccf8267e4811e6f \
"

NVIDIA_MODPROBE_VERSION = "396.51"

SRC_URI = "git://github.com/NVIDIA/libnvidia-container.git;protocol=https;name=libnvidia;branch=jetson \
           git://github.com/NVIDIA/nvidia-modprobe.git;protocol=https;branch=main;name=modprobe;destsuffix=git/deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION} \
           file://0001-Fix-mapping-of-library-paths-for-jetson-mounts.patch \
           file://0002-OE-cross-build-fixups.patch \
           file://0003-Add-support-for-separate-pass-through-tree.patch \
           "

# tag: v0.11.0+jetpack
SRCREV_libnvidia = "1b60893021cd00c87f201d11eb207215afa3ab11"
# Nvidia modprobe version 396.51
SRCREV_modprobe = "d97c08af5061f1516fb2e3a26508936f69d6d71d"
SRCREV_FORMAT = "libnvidia_modprobe"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= ""
PACKAGECONFIG[seccomp] = "WITH_SECCOMP=yes,WITH_SECCOMP=no,libseccomp"

def build_date(d):
    import datetime
    epoch = d.getVar('SOURCE_DATE_EPOCH')
    if epoch:
        dt = datetime.datetime.fromtimestamp(int(epoch), tz=datetime.timezone.utc)
        return 'DATE=' + dt.isoformat(timespec='minutes')
    return ''

# This must match the setting in tegra-container-passthrough recipe
PASSTHRU_ROOT = "${datadir}/nvidia-container-passthrough"

EXTRA_OEMAKE = "EXCLUDE_BUILD_FLAGS=1 PASSTHRU_ROOT=${PASSTHRU_ROOT} PLATFORM=${HOST_ARCH} JETSON=TRUE WITH_LIBELF=yes COMPILER=${@d.getVar('CC').split()[0]} REVISION=${SRCREV_libnvidia} ${@build_date(d)} ${PACKAGECONFIG_CONFARGS}"

CFLAGS:prepend = " -I=/usr/include/tirpc-1.2.6 "

export OBJCPY="${OBJCOPY}"

# Fix me: Create an independent recipe for nvidia-modprobe
do_configure:append() {
    # Mark Nvidia modprobe as downloaded
    touch ${S}/deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION}/.download_stamp
}

do_install () {
    oe_runmake install DESTDIR=${D}
    # Remove the development files, as this library is dlopened for
    # special processing by the normal libnvidia-container-tools
    rm -f ${D}${libdir}/libnvidia-container.so ${D}${libdir}/libnvidia-container.a
    rm -rf ${D}${libdir}/pkgconfig ${D}${includedir}
    # See note about licensing above
    find ${D}${datadir}/doc -type f -name 'COPYING*' -delete
}

RDEPENDS:${PN} = "tegra-container-passthrough"
