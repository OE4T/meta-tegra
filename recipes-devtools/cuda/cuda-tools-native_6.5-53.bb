require cuda-shared-binaries-native-${PV}.inc

CUDA_PKGS = " \
    core \
    misc-headers \
    cublas-dev \
    cufft-dev \
    curand-dev \
    cusparse-dev \
    npp-dev \
    cudart-dev \
"
do_install() {
    install -d ${D}${STAGING_DIR_NATIVE}    
    for pkg in ${CUDA_PKGS}; do
        dpkg-deb --extract ${S}/var/cuda-repo-6-5-local/cuda-$pkg-6-5_${PV}_amd64.deb ${D}${STAGING_DIR_NATIVE}
    done
    rm -rf ${D}${STAGING_DIR_NATIVE}/usr/share
    rm -rf ${D}${STAGING_DIR_NATIVE}/usr/lib # don't want the pkgconfig files for cross builds, only native
}
