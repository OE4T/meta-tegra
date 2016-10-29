require cuda-shared-binaries-${PV}.inc

CUDA_CUDART_PKGS = " \
    cudart \
    cudart-dev \
    driver-dev \
    misc-headers \
"
do_compile() {

    for pkg in ${CUDA_CUDART_PKGS}; do
        dpkg-deb --extract ${S}/var/cuda-repo-6-5-local/cuda-${pkg}-6-5_${PV}_armhf.deb ${B}
    done

    rm -rf ${B}/usr/share

    sed -i -e'/^#error -- unsupported GNU.*4\.9 and up/d' ${B}/usr/local/cuda-6.5/targets/armv7-linux-gnueabihf/include/host_config.h

    for f in ${B}/usr/lib/pkgconfig/*; do
        sed -i -re's,^(libdir=.*/)lib[^/]*$,\1${baselib},' $f
        sed -i -re's,^(libdir=.*/)lib[^/]*(/.*)$,\1${baselib}\2,' $f
        sed -i -re's!^(Libs:.*)!\1 -Wl,-rpath=$!' $f
        sed -i -re's,^(Libs:.*),\1{libdir},' $f
    done
}

do_install() {
    install -d ${D}${prefix}/local/cuda-6.5/include
    install -d ${D}${prefix}/local/cuda-6.5/${baselib}
    install -d ${D}${libdir}/pkgconfig
    cp -R --preserve=mode,timestamps ${B}/usr/local/cuda-6.5/targets/armv7-linux-gnueabihf/lib/* ${D}${prefix}/local/cuda-6.5/${baselib}/
    cp -R --preserve=mode,timestamps ${B}/usr/local/cuda-6.5/targets/armv7-linux-gnueabihf/include/* ${D}${prefix}/local/cuda-6.5/include/
    for f in ${B}/usr/lib/pkgconfig/*; do
        install -m0644 $f ${D}${libdir}/pkgconfig/
    done
}

FILES_${PN} = "${prefix}/local/cuda-6.5/${baselib}/*.so*"
FILES_${PN}-dev = "${prefix}/local/cuda-6.5/include ${prefix}/local/cuda-6.5/${baselib}/*.a ${prefix}/local/cuda-6.5/${baselib}/stubs ${libdir}"

INSANE_SKIP_${PN} += "dev-so"
INSANE_SKIP_${PN}-dev = "ldflags staticdev libdir dev-elf"
