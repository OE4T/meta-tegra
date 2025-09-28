SUMMARY = "NVIDIA container runtime library"

DESCRIPTION = "NVIDIA container runtime library \
The nvidia-container library provides an interface to configure GNU/Linux \
containers leveraging NVIDIA hardware. The implementation relies on several \
kernel subsystems and is designed to be agnostic of the container runtime. \
"
HOMEPAGE = "https://github.com/NVIDIA/libnvidia-container"

inherit go

DEPENDS = " \
    coreutils-native \
    pkgconfig-native \
    libcap \
    elfutils \
    libtirpc \
    ldconfig-native \
"
LICENSE = "Apache-2.0 & MIT"

# Both source repositories include GPL COPYING (and for
# libnvidia-container, COPYING.LESSER) files. However:
# * For libnvidia-container, those files might only apply if elfutils
#   sources were included (the makefile has commands to download and
#   build libelf from elfutils sources). We configure the build to
#   use libelf provided externally.
# * For nvidia-modprobe, only the nvidia-modprobe-utils library is
#   built and used.  All sources for that library are MIT-licensed.

LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION}/modprobe-utils/nvidia-modprobe-utils.c;endline=22;md5=b6a3106a81660c726888d006853ada63 \
    file://deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION}/modprobe-utils/pci-enum.h;endline=29;md5=ca948b6fabc48e616fccbf17247feebf \
    file://deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION}/modprobe-utils/pci-sysfs.c;endline=25;md5=0449248350efd54938e7f8d25af965cb \
"

NVIDIA_MODPROBE_VERSION = "550.54.14"

NVIDIA_MODPROBE_SRCURI_DESTSUFFIX = "${@os.path.join(os.path.basename(d.getVar('S')), 'deps', 'src', 'nvidia-modprobe-' + d.getVar('NVIDIA_MODPROBE_VERSION')) + '/'}"

SRC_URI = "git://github.com/NVIDIA/libnvidia-container.git;protocol=https;name=libnvidia;branch=main \
           git://github.com/NVIDIA/nvidia-modprobe.git;protocol=https;branch=main;name=modprobe;destsuffix=${NVIDIA_MODPROBE_SRCURI_DESTSUFFIX} \
           file://0001-OE-cross-build-fixups.patch \
"

# tag: v1.18.0-rc.1
SRCREV_libnvidia = "9d6a23b99689663e245b802c3eeedcddd658abdd"
# Nvidia modprobe version 550.54.14
SRCREV_modprobe = "149440ca0654d928f27df5ebff485a122bfe43b1"
SRCREV_FORMAT = "libnvidia_modprobe"

B = "${S}"

PACKAGECONFIG ??= ""
PACKAGECONFIG[seccomp] = "WITH_SECCOMP=yes,WITH_SECCOMP=no,libseccomp"

# We need to link with libelf, otherwise we need to
# include bmake-native which does not exist at the moment.
EXTRA_OEMAKE = 'EXCLUDE_BUILD_FLAGS=1 PLATFORM=${HOST_ARCH} WITH_LIBELF=yes COMPILER=${@d.getVar('CC').split()[0]} REVISION=${SRCREV_libnvidia} ${PACKAGECONFIG_CONFARGS} \
                NVIDIA_MODPROBE_EXTRA_CFLAGS="${NVIDIA_MODPROBE_EXTRA_CFLAGS}"'
NVIDIA_MODPROBE_EXTRA_CFLAGS ?= "${DEBUG_PREFIX_MAP}"

export OBJCPY = "${OBJCOPY}"

patch_nv_modprobe() {
    patch -d ${UNPACKDIR}/${NVIDIA_MODPROBE_SRCURI_DESTSUFFIX} -p1 < ${S}/mk/nvidia-modprobe.patch
    touch ${UNPACKDIR}/${NVIDIA_MODPROBE_SRCURI_DESTSUFFIX}/.download_stamp
}

do_patch[postfuncs] += "patch_nv_modprobe"

do_configure() {
    base_do_configure
}

do_compile() {
    export TMPDIR="${GOTMPDIR}"
    base_do_compile
}

do_install () {
    oe_runmake install DESTDIR=${D}
    # See note about licensing above
    find ${D}${datadir}/doc -type f -name 'COPYING*' -delete
}

PACKAGES =+ "${PN}-tools"
FILES:${PN}-tools = "${bindir}"
# XXX - go.bbclass rewrites these
FILES:${PN}-dev = "${includedir} ${FILES_SOLIBSDEV} ${libdir}/*.la \
                ${libdir}/*.o ${libdir}/pkgconfig ${datadir}/pkgconfig \
                ${datadir}/aclocal ${base_libdir}/*.o \
                ${libdir}/${BPN}/*.la ${base_libdir}/*.la \
                ${libdir}/cmake ${datadir}/cmake"
FILES:${PN}-staticdev = "${libdir}/*.a ${base_libdir}/*.a ${libdir}/${BPN}/*.a"
# - XXX
INSANE_SKIP:${PN} = "already-stripped ldflags"
RDEPENDS:${PN}:append:tegra = " ldconfig tegra-libraries-cuda"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
