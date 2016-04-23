require cuda-shared-binaries.inc

do_compile() {
    dpkg-deb --extract ${S}/var/cuda-repo-7-0-local/cuda-cudart-7-0_${PV}_armhf.deb ${B}
    dpkg-deb --extract ${S}/var/cuda-repo-7-0-local/cuda-cudart-dev-7-0_${PV}_armhf.deb ${B}
    dpkg-deb --extract ${S}/var/cuda-repo-7-0-local/cuda-driver-dev-7-0_${PV}_armhf.deb ${B}
    dpkg-deb --extract ${S}/var/cuda-repo-7-0-local/cuda-misc-headers-7-0_${PV}_armhf.deb ${B}
    rm -rf ${B}/usr/share
}

do_install() {
    install -d ${D}${prefix}/local/cuda-7.0/include
    install -d ${D}${prefix}/local/cuda-7.0/lib
    install -d ${D}${libdir}/pkgconfig
    cp -R --preserve=mode,timestamps ${B}/usr/local/cuda-7.0/targets/armv7-linux-gnueabihf/lib/* ${D}${prefix}/local/cuda-7.0/lib/
    cp -R --preserve=mode,timestamps ${B}/usr/local/cuda-7.0/targets/armv7-linux-gnueabihf/include/* ${D}${prefix}/local/cuda-7.0/include/
    for f in ${B}/usr/lib/pkgconfig/*; do
        install -m0644 $f ${D}${libdir}/pkgconfig/
    done
}

FILES_${PN} = "${prefix}/local/cuda-7.0/lib/*.so*"
FILES_${PN}-dev = "${prefix}/local/cuda-7.0/include ${prefix}/local/cuda-7.0/lib/*.a ${prefix}/local/cuda-7.0/lib/stubs ${libdir}"

INSANE_SKIP_${PN} += "dev-so"
INSANE_SKIP_${PN}-dev = "ldflags staticdev libdir dev-elf"
