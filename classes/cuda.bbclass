PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
DEPENDS_append = " cuda-toolkit cuda-tools-native"
EXTRA_OECMAKE_append = " -DCUDA_TOOLKIT_TARGET_DIR=${STAGING_DIR_TARGET}/usr/local/cuda-${CUDA_VERSION} \
                         -DCUDA_TOOLKIT_ROOT_DIR=${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION}"
RDEPENDS_${PN}_append = " cuda-cudart"
LDFLAGS_append = " -Wl,-rpath-link,${STAGING_DIR_TARGET}/usr/local/cuda-${CUDA_VERSION}/lib -Wl,-rpath,/usr/local/cuda-${CUDA_VERSION}/lib"
