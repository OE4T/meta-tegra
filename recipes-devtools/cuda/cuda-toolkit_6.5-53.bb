require cuda-shared-binaries-${PV}.inc

CUDA_PKGS = "           \
    command-line-tools  \
    cusolver            \
    cusolver-dev        \
    cublas              \
    cublas-dev          \
    cufft               \
    cufft-dev           \
    curand              \
    curand-dev          \
    cusparse            \
    cusparse-dev        \
    npp                 \
    npp-dev             \
    core                \
"

DEPENDS = "cuda-cudart"
RDEPENDS_${PN} = "tegra-libraries"
RDEPENDS_${PN}-dev = "${PN} expat ncurses"

do_compile() {
    for pkg in ${CUDA_PKGS}; do
      dpkg-deb --extract ${S}/var/cuda-repo-6-5-local/cuda-$pkg-6-5_${PV}_armhf.deb ${B}
    done
    rm -rf ${B}/usr/share
}

do_install() {
    install -d ${D}${prefix}/local/cuda-6.5/include
    install -d ${D}${prefix}/local/cuda-6.5/lib
    install -d ${D}${libdir}/pkgconfig
    cp -R --preserve=mode,timestamps ${B}/usr/local/cuda-6.5/targets/armv7-linux-gnueabihf/lib/* ${D}${prefix}/local/cuda-6.5/lib/
    cp -R --preserve=mode,timestamps ${B}/usr/local/cuda-6.5/targets/armv7-linux-gnueabihf/include/* ${D}${prefix}/local/cuda-6.5/include/
    for f in bin nvvm open64; do
        cp -R --preserve=mode,timestamps ${B}/usr/local/cuda-6.5/${f} ${D}${prefix}/local/cuda-6.5/
    done
    for f in ${B}/usr/lib/pkgconfig/*; do
        install -m0644 $f ${D}${libdir}/pkgconfig/
    done
}

FILES_${PN} = "${prefix}/local/cuda-6.5/lib/*${SOLIBS} ${prefix}/local/cuda-6.5/lib/stubs"
FILES_${PN}-staticdev = "${prefix}/local/cuda-6.5/lib/*.a"
FILES_${PN}-dev = "\
    ${prefix}/local/cuda-6.5/include                \
    ${libdir}                                       \
    ${prefix}/local/cuda-6.5/lib/*.so               \
    ${prefix}/local/cuda-6.5/bin                    \
    ${prefix}/local/cuda-6.5/nvvm                   \
    ${prefix}/local/cuda-6.5/open64                 \
    ${prefix}/local/cuda etc/profile.d/*            \
    "

INSANE_SKIP_${PN} += "dev-so dev-deps textrel"
INSANE_SKIP_${PN}-dev = "ldflags libdir dev-elf textrel"

COMPATIBLE_MACHINE = "(tegra124)"
