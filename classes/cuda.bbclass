CUDA_NVCC_COMPAT_FLAGS ??= ""
CUDA_NVCC_PATH_FLAGS ??= "--include-path ${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}/include --library-path ${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}/${baselib}"
CUDA_NVCC_EXTRA_FLAGS ??= ""
CUDA_NVCC_FLAGS ?= "${CUDA_NVCC_ARCH_FLAGS} ${CUDA_NVCC_COMPAT_FLAGS} ${CUDA_NVCC_PATH_FLAGS} ${CUDA_NVCC_EXTRA_FLAGS}"

CUDA_CXXFLAGS = "-I=/usr/local/cuda-${CUDA_VERSION}/include"
CUDA_LDFLAGS = "\
   -L=/usr/local/cuda-${CUDA_VERSION}/${baselib} -L=/usr/local/cuda-${CUDA_VERSION}/${baselib}/stubs \
  -Wl,-rpath-link,${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}/${baselib} \
  -Wl,-rpath,/usr/local/cuda-${CUDA_VERSION}/${baselib} \
"

LDFLAGS_prepend_cuda = "${TOOLCHAIN_OPTIONS} "
LDFLAGS_append_cuda = " ${CUDA_LDFLAGS}"

export CUDAHOSTCXX = "${@d.getVar('CXX').split()[0]}"
export CUDAFLAGS = "${CUDA_NVCC_FLAGS} ${@' '.join(['-Xcompiler ' + arg for arg in d.getVar('CXX').split()[1:]])}"

# The following are for the old-style FindCUDA.cmake module (pre-3.8)
CUDA_EXTRA_OECMAKE = '\
  -DCUDA_TOOLKIT_TARGET_DIR=${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION} \
  -DCUDA_TOOLKIT_ROOT_DIR=${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION} \
  -DCUDA_NVCC_FLAGS="${CUDA_NVCC_FLAGS}" \
'
EXTRA_OECMAKE_append_cuda = " ${CUDA_EXTRA_OECMAKE}"

export CUDA_TOOLKIT_ROOT = "${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION}"
export CUDA_NVCC_EXECUTABLE = "${CUDA_TOOLKIT_ROOT}/bin/nvcc"
export CUDA_PATH = "${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}"

CUDA_NATIVEDEPS = "cuda-compiler-native cuda-cudart-native"
CUDA_NATIVEDEPS_class-native = ""
CUDA_DEPENDS = "cuda-toolkit ${CUDA_NATIVEDEPS}"

DEPENDS_append_cuda = " ${CUDA_DEPENDS}"
PATH_append_cuda = ":${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION}/bin"

# The following are for the new-style (CMake 3.8+) CUDA language support
cmake_do_generate_toolchain_file_append_cuda() {
    cat >> ${WORKDIR}/toolchain.cmake <<EOF
set(CMAKE_CUDA_TOOLKIT_ROOT_DIR "${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION}" CACHE PATH "" FORCE)
set(CMAKE_CUDA_TOOLKIT_TARGET_DIR "${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}" CACHE PATH "" FORCE)
set(CMAKE_CUDA_TOOLKIT_INCLUDE_DIRECTORIES "${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}/include" CACHE PATH "" FORCE)
EOF
}

PACKAGE_ARCH_cuda = "${SOC_FAMILY_PKGARCH}"
RDEPENDS_${PN}_append_tegra = " tegra-libraries"
RRECOMMENDS_${PN}_append_tegra = " kernel-module-nvgpu"

