CUDA_NVCC_COMPAT_FLAGS ??= ""
CUDA_NVCC_PATH_FLAGS ??= "--include-path ${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}/include --library-path ${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}/lib"
CUDA_NVCC_EXTRA_FLAGS ??= ""
CUDA_NVCC_FLAGS ?= "${CUDA_NVCC_ARCH_FLAGS} ${CUDA_NVCC_COMPAT_FLAGS} ${CUDA_NVCC_PATH_FLAGS} ${CUDA_NVCC_EXTRA_FLAGS}"

CUDA_CXXFLAGS = "-I=/usr/local/cuda-${CUDA_VERSION}/include"
CUDA_LDFLAGS = "\
   -L=/usr/local/cuda-${CUDA_VERSION}/lib -L=/usr/local/cuda-${CUDA_VERSION}/lib/stubs \
  -Wl,-rpath-link,${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}/lib \
  -Wl,-rpath,/usr/local/cuda-${CUDA_VERSION}/lib \
"

CUDA_EXTRA_OECMAKE = '\
  -DCUDA_TOOLKIT_TARGET_DIR=${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION} \
  -DCUDA_TOOLKIT_TARGET_DIR_INTERNAL=${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION} \
  -DCUDA_TOOLKIT_ROOT_DIR=${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION} \
  -DCUDA_TOOLKIT_ROOT_DIR_INTERNAL=${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION} \
  -DCUDA_NVCC_FLAGS="${CUDA_NVCC_FLAGS}" \
'

CUDA_DEPENDS = "cuda-toolkit cuda-tools-native"

DEPENDS_append_cuda = " ${CUDA_DEPENDS}"
LDFLAGS_append_cuda = " ${CUDA_LDFLAGS}"
CXXFLAGS_append_cuda = " ${CUDA_CXXFLAGS}"
EXTRA_OECMAKE_append_cuda = " ${CUDA_EXTRA_OECMAKE}"
PATH_append_cuda = ":${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION}/bin"

def cuda_host_compiler_flags(d):
    result = ["-Xcompiler {}".format(arg) for arg in d.getVar('CXXFLAGS').split()]
    return ' '.join(result)

OECMAKE_CUDA_COMPILER ?= "nvcc"
OECMAKE_CUDA_FLAGS ?= "${CUDA_NVCC_FLAGS} ${@cuda_host_compiler_flags(d)}"
OECMAKE_CUDA_LINK_FLAGS ?= "${OECMAKE_CXX_LINK_FLAGS}"
OECMAKE_CUDA_LIBRARIES ?= "-lcudadevrt -lcudart_static -lrt -lpthread -ldl"

PACKAGE_ARCH_cuda = "${SOC_FAMILY_PKGARCH}"
RDEPENDS_${PN}_append_tegra = " tegra-libraries kernel-module-nvgpu"

cmake_do_generate_toolchain_file_append_cuda() {
    cat >> ${WORKDIR}/toolchain.cmake <<EOF
set(CMAKE_CUDA_COMPILER "${OECMAKE_CUDA_COMPILER}")
set(CMAKE_CUDA_HOST_COMPILER "${OECMAKE_CXX_COMPILER}")
set(CMAKE_CUDA_FLAGS "${OECMAKE_CUDA_FLAGS}" CACHE STRING "CUDAFLAGS")
set(CMAKE_CUDA_LINK_FLAGS "${OECMAKE_CUDA_LINK_FLAGS}" CACHE STRING "LDFLAGS")
set(CMAKE_CUDA_LINK_EXECUTABLE "<CMAKE_CUDA_HOST_LINK_LAUNCHER> <CMAKE_CUDA_LINK_FLAGS> <LINK_FLAGS> <OBJECTS> -o <TARGET> <LINK_LIBRARIES> ${OECMAKE_CUDA_LIBRARIES}")
set(CMAKE_CUDA_TOOLKIT_INCLUDE_DIRECTORIES "${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}/include")
set(CMAKE_CUDA_LINKER_WRAPPER_FLAG "-Xlinker" " ")
set(CMAKE_CUDA_LINKER_WRAPPER_FLAG_SEP ",")
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
