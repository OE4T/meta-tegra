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
"

DEPENDS = "cuda-cudart"

do_compile() {
    for pkg in ${CUDA_PKGS}; do
      dpkg-deb --extract ${S}/var/cuda-repo-7-0-local/cuda-$pkg-7-0_${PV}_arm64.deb ${B}
    done
    rm -rf ${B}/usr/share
}

do_install() {
    install -d ${D}${prefix}/local/cuda-7.0/include
    install -d ${D}${prefix}/local/cuda-7.0/lib
    install -d ${D}${libdir}/pkgconfig
    cp -R --preserve=mode,timestamps ${B}/usr/local/cuda-7.0/targets/aarch64-linux/lib/* ${D}${prefix}/local/cuda-7.0/lib/
    cp -R --preserve=mode,timestamps ${B}/usr/local/cuda-7.0/targets/aarch64-linux/include/* ${D}${prefix}/local/cuda-7.0/include/
    for f in ${B}/usr/lib/pkgconfig/*; do
        install -m0644 $f ${D}${libdir}/pkgconfig/
    done
}

FILES_${PN} = "${prefix}/local/cuda-7.0/lib/*.so*"
FILES_${PN}-dev = "${prefix}/local/cuda-7.0/include ${prefix}/local/cuda-7.0/lib/*.a ${prefix}/local/cuda-7.0/lib/stubs ${libdir}"

INSANE_SKIP_${PN} += "dev-so dev-deps"
INSANE_SKIP_${PN}-dev = "ldflags staticdev libdir dev-elf"
