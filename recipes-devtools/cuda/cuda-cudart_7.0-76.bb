require cuda-shared-binaries.inc

CUDA_CUDART_PKGS = " \
    cudart \
    cudart-dev \
    driver-dev \
    misc-headers \
"
do_compile() {

    for pkg in ${CUDA_CUDART_PKGS}; do
        dpkg-deb --extract ${S}/var/cuda-repo-7-0-local/cuda-${pkg}-7-0_${PV}_arm64.deb ${B}
    done

    rm -rf ${B}/usr/share

    for f in ${B}/usr/lib/pkgconfig/*; do
        sed -i -re's,^(libdir=.*/)lib[^/]*$,\1${baselib},' $f
        sed -i -re's,^(libdir=.*/)lib[^/]*(/.*)$,\1${baselib}\2,' $f
        sed -i -re's!^(Libs:.*)!\1 -Wl,-rpath=$!' $f
        sed -i -re's,^(Libs:.*),\1{libdir},' $f
    done
}

do_install() {
    install -d ${D}${prefix}/local/cuda-7.0/include
    install -d ${D}${prefix}/local/cuda-7.0/${baselib}
    install -d ${D}${libdir}/pkgconfig
    cp -R --preserve=mode,timestamps ${B}/usr/local/cuda-7.0/targets/aarch64-linux/lib/* ${D}${prefix}/local/cuda-7.0/${baselib}/
    cp -R --preserve=mode,timestamps ${B}/usr/local/cuda-7.0/targets/aarch64-linux/include/* ${D}${prefix}/local/cuda-7.0/include/
    for f in ${B}/usr/lib/pkgconfig/*; do
        install -m0644 $f ${D}${libdir}/pkgconfig/
    done
}

FILES_${PN} = "${prefix}/local/cuda-7.0/${baselib}/*.so*"
FILES_${PN}-dev = "${prefix}/local/cuda-7.0/include ${prefix}/local/cuda-7.0/${baselib}/*.a ${prefix}/local/cuda-7.0/${baselib}/stubs ${libdir}"

INSANE_SKIP_${PN} += "dev-so"
INSANE_SKIP_${PN}-dev = "ldflags staticdev libdir dev-elf"
