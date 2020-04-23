CUDA_PKGNAMES = "libcublas10_10.2.2.89-1_${CUDA_DEB_PKGARCH}.deb"
CUDA_DEV_PKGNAMES = "libcublas-dev_10.2.2.89-1_${CUDA_DEB_PKGARCH}.deb"
CUDA_FULL_VERSION = "10.2.89-1"

require cuda-shared-binaries-${CUDA_FULL_VERSION}.inc


do_compile_append() {
    mv ${B}/usr/${baselib}/${HOST_ARCH}-linux-gnu/lib* ${B}/usr/${baselib}/
    rm -rf ${B}/usr/${baselib}/${HOST_ARCH}-linux-gnu/stubs
    rmdir ${B}/usr/${baselib}/${HOST_ARCH}-linux-gnu
    sed -i -e's,^pkgroot=.*,prefix=${prefix},' -e's,{pkgroot},{prefix},g' \
	-e's! -Wl,-rpath.*!!' ${B}/usr/${baselib}/pkgconfig/cublas-10.pc
    ln -s cublas-10.pc ${B}/usr/${baselib}/pkgconfig/cublas-10.2.pc
}

CONTAINER_CSV_FILES = "${libdir}/*.so*"

BBCLASSEXTEND = "native nativesdk"
