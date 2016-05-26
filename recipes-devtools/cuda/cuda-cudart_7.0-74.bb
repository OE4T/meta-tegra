require cuda-shared-binaries.inc

do_compile() {
    dpkg-deb --extract ${S}/var/cuda-repo-7-0-local/cuda-cudart-7-0_${PV}_arm64.deb ${B}
    dpkg-deb --extract ${S}/var/cuda-repo-7-0-local/cuda-cudart-dev-7-0_${PV}_arm64.deb ${B}
    dpkg-deb --extract ${S}/var/cuda-repo-7-0-local/cuda-driver-dev-7-0_${PV}_arm64.deb ${B}
    dpkg-deb --extract ${S}/var/cuda-repo-7-0-local/cuda-misc-headers-7-0_${PV}_arm64.deb ${B}
    rm -rf ${B}/usr/share
    for f in ${B}/usr/lib/pkgconfig/*; do
        sed -i -re's,^(libdir=.*/)lib[^/]*$,\1${baselib},' $f
        sed -i -re's,^(libdir=.*/)lib[^/]*(/.*)$,\1${baselib}\2,' $f
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
