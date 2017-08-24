CUDA_LDFLAGS = "\
  -Wl,-rpath-link,${STAGING_DIR_TARGET}/usr/local/cuda-${CUDA_VERSION}/lib \
  -Wl,-rpath,/usr/local/cuda-${CUDA_VERSION}/lib \
"

CUDA_EXTRA_OECMAKE = '\
  -DCUDA_TOOLKIT_TARGET_DIR=${STAGING_DIR_TARGET}/usr/local/cuda-${CUDA_VERSION} \
  -DCUDA_TOOLKIT_TARGET_DIR_INTERNAL=${STAGING_DIR_TARGET}/usr/local/cuda-${CUDA_VERSION} \
  -DCUDA_TOOLKIT_ROOT_DIR=${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION} \
  -DCUDA_TOOLKIT_ROOT_DIR_INTERNAL=${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION} \
  -DCUDA_NVCC_FLAGS="${CUDA_NVCC_FLAGS}" \
'

CUDA_DEPENDS = "cuda-toolkit cuda-tools-native"

DEPENDS_append_cuda = " ${CUDA_DEPENDS}"
LDFLAGS_append_cuda = " ${CUDA_LDFLAGS}"
EXTRA_OECMAKE_append_cuda = " ${CUDA_EXTRA_OECMAKE}"

PACKAGE_ARCH_cuda = "${SOC_FAMILY_PKGARCH}"

cmake_do_generate_toolchain_file_append() {
    cat >> ${WORKDIR}/toolchain.cmake <<EOF
if(NOT COMMAND find_host_package)
  macro(find_host_package)
    find_package(\${ARGN})
  endmacro()
endif()
if(NOT COMMAND find_host_program)
  macro(find_host_program)
    find_program(\${ARGN})
  endmacro()
endif()
EOF
}
