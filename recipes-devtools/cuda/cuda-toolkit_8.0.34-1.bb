require cuda-shared-binaries.inc

CUDA_PKGS = " \
    nvrtc \
    nvrtc-dev \
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
    command-line-tools \
    core \
"

DEPENDS = "cuda-cudart"
RDEPENDS_${PN} = "tegra-libraries"

do_compile() {
    for pkg in ${CUDA_PKGS}; do
      dpkg-deb --extract ${S}/var/cuda-repo-8-0-local/cuda-$pkg-8-0_${PV}_arm64.deb ${B}
    done
    rm -rf ${B}/usr/share
}

do_install() {
    install -d ${D}${prefix}/local/cuda-8.0/include
    install -d ${D}${prefix}/local/cuda-8.0/lib
    install -d ${D}${prefix}/local/cuda-8.0/bin
    install -d ${D}${libdir}/pkgconfig
    cp -R --preserve=mode,timestamps ${B}/usr/local/cuda-8.0/targets/aarch64-linux/lib/* ${D}${prefix}/local/cuda-8.0/lib/
    cp -R --preserve=mode,timestamps ${B}/usr/local/cuda-8.0/targets/aarch64-linux/include/* ${D}${prefix}/local/cuda-8.0/include/
    cp -R --preserve=mode,timestamps ${B}/usr/local/cuda-8.0/bin/* ${D}${prefix}/local/cuda-8.0/bin
    for f in ${B}/usr/lib/pkgconfig/*; do
        install -m0644 $f ${D}${libdir}/pkgconfig/
    done
}

FILES_${PN} = "${prefix}/local/cuda-8.0/lib/*${SOLIBS} ${prefix}/local/cuda-8.0/lib/stubs"
FILES_${PN}-staticdev = "${prefix}/local/cuda-8.0/lib/*.a"
FILES_${PN}-dev = "${prefix}/local/cuda-8.0/include ${libdir} ${prefix}/local/cuda-8.0/lib/*.so ${prefix}/local/cuda-8.0/bin"

INSANE_SKIP_${PN} += "dev-so dev-deps"
INSANE_SKIP_${PN}-dev = "ldflags libdir dev-elf"
