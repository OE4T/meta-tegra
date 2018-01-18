require cuda-shared-binaries-${PV}.inc

CUDA_PKGS = " \
    cusolver \
    cusolver-dev \
    cublas \
    cublas-dev \
    cufft \
    cufft-dev \
    curand \
    curand-dev \
    cusparse \
    cusparse-dev \
    npp \
    npp-dev \
"

DEPENDS = "cuda-cudart"
RDEPENDS_${PN} = "tegra-libraries"

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
    for f in ${B}/usr/lib/pkgconfig/*; do
        install -m0644 $f ${D}${libdir}/pkgconfig/
    done
}

FILES_${PN} = "${prefix}/local/cuda-6.5/lib/*${SOLIBS} ${prefix}/local/cuda-6.5/lib/stubs"
FILES_${PN}-staticdev = "${prefix}/local/cuda-6.5/lib/*.a"
FILES_${PN}-dev = "${prefix}/local/cuda-6.5/include ${libdir} ${prefix}/local/cuda-6.5/lib/*.so"

INSANE_SKIP_${PN} += "dev-so dev-deps textrel"
INSANE_SKIP_${PN}-dev = "ldflags libdir dev-elf"
COMPATIBLE_MACHINE = "(tegra124)"
