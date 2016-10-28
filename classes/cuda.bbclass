PACKAGE_ARCH_tegra210 = "${SOC_FAMILY_PKGARCH}"
DEPENDS_append_tegra210 = " cuda-toolkit cuda-tools-native"
EXTRA_OECMAKE_append_tegra210 = " -DCUDA_TOOLKIT_TARGET_DIR=${STAGING_DIR_TARGET}/usr/local/cuda-${CUDA_VERSION} \
                         -DCUDA_TOOLKIT_ROOT_DIR=${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION}"
RDEPENDS_${PN}_append_tegra210 = " cuda-cudart"
LDFLAGS_append_tegra210 = " -Wl,-rpath-link,${STAGING_DIR_TARGET}/usr/local/cuda-${CUDA_VERSION}/lib -Wl,-rpath,/usr/local/cuda-${CUDA_VERSION}/lib"
