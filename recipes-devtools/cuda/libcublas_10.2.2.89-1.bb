CUDA_PKGNAMES = "libcublas10_10.2.2.89-1_${CUDA_DEB_PKGARCH}.deb libcublas-dev_10.2.2.89-1_${CUDA_DEB_PKGARCH}.deb"
CUDA_FULL_VERSION = "10.2.89-1"

require cuda-shared-binaries-${CUDA_FULL_VERSION}.inc

L4T_DEB_GROUP = "cublas"
SRC_COMMON_DEBS = "libcublas10_10.2.2.89-1_${CUDA_DEB_PKGARCH}.deb;name=main;subdir=${BP} \
                   libcublas-dev_10.2.2.89-1_${CUDA_DEB_PKGARCH}.deb;name=dev;subdir=${BP}"
SRC_URI_append = " ${L4T_DEB_FEED_BASE}/common/pool/main/c/cuda/cuda-license-${CUDA_VERSION_DASHED}_10.2.89-1_${CUDA_DEB_PKGARCH}.deb;name=lic;subdir=${BP}"
MAINSUM = "d0299b139a163136432dfb2c028769944b6c5636ad9238614860c196a1c91aea"
MAINSUM_x86-64 = "e28a243120d72ddb4dd75dfa3894330394318df87ebe5f1d4611b0ca0e8ade2b"
DEVSUM = "5fa7e3e8fe266fdea7e91778610b7e8d3d85d8950875a4915ce3626c9e564365"
DEVSUM_x86-64 = "2d36e90ca3c1835577bdea35db5980058c3eb35b24961c8764cdd2fa607cad7d"

do_compile_append() {
    if [ -d ${B}/usr/${baselib}/${HOST_ARCH}-linux-gnu ]; then
        for f in ${B}/usr/${baselib}/${HOST_ARCH}-linux-gnu/lib*; do
            [ -e "$f" ] || continue
            mv "$f"  ${B}/usr/${baselib}/
        done
        rm -rf ${B}/usr/${baselib}/${HOST_ARCH}-linux-gnu
    fi
    sed -i -e's,^pkgroot=.*,prefix=${prefix},' -e's,{pkgroot},{prefix},g' \
	-e's! -Wl,-rpath.*!!' ${B}/usr/${baselib}/pkgconfig/cublas-10.pc
    ln -sf cublas-10.pc ${B}/usr/${baselib}/pkgconfig/cublas-10.2.pc
}

do_install_append() {
    rm -rf ${D}${prefix}/local
}

CONTAINER_CSV_FILES = "${libdir}/*.so*"

BBCLASSEXTEND = "native nativesdk"
