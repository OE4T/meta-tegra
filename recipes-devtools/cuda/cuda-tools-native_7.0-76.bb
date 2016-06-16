require cuda-shared-binaries-native.inc

CUDA_PKGS = " \
    core \
    misc-headers \
    nvrtc-dev \
    cusolver-dev \
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
        dpkg-deb --extract ${S}/var/cuda-repo-7-0-local/cuda-$pkg-7-0_${PV}_amd64.deb ${D}${STAGING_DIR_NATIVE}
    done
    rm -rf ${D}${STAGING_DIR_NATIVE}/usr/share
    rm -rf ${D}${STAGING_DIR_NATIVE}/usr/lib # don't want the pkgconfig files for cross builds, only native
}
