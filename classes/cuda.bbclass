CUDA_NVCC_COMPAT_FLAGS ??= ""
CUDA_NVCC_PATH_FLAGS ??= "--include-path ${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}/include --library-path ${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}/${baselib}"
CUDA_NVCC_EXTRA_FLAGS ??= ""
CUDA_NVCC_FLAGS ?= "${CUDA_NVCC_ARCH_FLAGS} ${CUDA_NVCC_COMPAT_FLAGS} ${CUDA_NVCC_PATH_FLAGS} ${CUDA_NVCC_EXTRA_FLAGS}"

CUDA_CXXFLAGS = "-I${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION}/include -I=/usr/local/cuda-${CUDA_VERSION}/include"
CUDA_LDFLAGS = "\
   -L=/usr/local/cuda-${CUDA_VERSION}/${baselib} -L=/usr/local/cuda-${CUDA_VERSION}/${baselib}/stubs \
  -Wl,-rpath-link,${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}/${baselib} \
  -Wl,-rpath,/usr/local/cuda-${CUDA_VERSION}/${baselib} \
"

LDFLAGS_prepend_cuda = "${TOOLCHAIN_OPTIONS} "
LDFLAGS_append_cuda = " ${CUDA_LDFLAGS}"

export CUDAHOSTCXX = "${@d.getVar('CXX').split()[0]}"
export CUDAFLAGS = "${CUDA_NVCC_FLAGS} ${@' '.join(['-Xcompiler ' + arg for arg in d.getVar('CXX').split()[1:]])}"

# meson uses 'CUFLAGS' for flags to pass to nvcc
# and requires all nvcc, compiler, and linker flags to be
# bundled into that one environment variable.
#
# Linker flags are passed via nvcc through directly to ld rather
# than the gcc/g++ driver, so we have to scan through LDFLAGS
# looking for linker options and strip them of the -Wl, prefix.
def cuda_meson_ldflags(d):
    linkargs = []
    args = (d.getVar('LDFLAGS') or '').split()
    for arg in args:
        if arg.startswith('-Wl,'):
            linkargs += [sub for sub in arg[4:].split(',')]
        elif arg.startswith('--sysroot='):
            linkargs.append(arg)
    return '-Xlinker ' + ','.join(linkargs)
CUFLAGS = "-ccbin ${@d.getVar('CXX').split()[0]} ${CUDAFLAGS} ${@' '.join(['-Xcompiler ' + arg for arg in d.getVar('CXXFLAGS').split()])} ${@cuda_meson_ldflags(d)}"

# The following are for the old-style FindCUDA.cmake module (pre-3.8)
CUDA_EXTRA_OECMAKE = '\
  -DCUDA_TOOLKIT_TARGET_DIR=${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION} \
  -DCUDA_TOOLKIT_ROOT_DIR=${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION} \
  -DCUDA_NVCC_FLAGS="${CUDA_NVCC_FLAGS}" \
'
EXTRA_OECMAKE_append_cuda = " ${CUDA_EXTRA_OECMAKE}"

export CUDA_TOOLKIT_ROOT = "${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION}"
export CUDA_NVCC_EXECUTABLE = "${CUDA_TOOLKIT_ROOT}/bin/nvcc"
export CUDACXX = "${CUDA_TOOLKIT_ROOT}/bin/nvcc"
export CUDA_PATH = "${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}"

CUDA_NATIVEDEPS = "cuda-compiler-native cuda-cudart-native"
CUDA_NATIVEDEPS_class-native = ""
CUDA_DEPENDS = "cuda-libraries ${CUDA_NATIVEDEPS}"

DEPENDS_append_cuda = " ${CUDA_DEPENDS} ${@'tegra-cmake-overrides' if bb.data.inherits_class('cmake', d) else ''}"
PATH_prepend_cuda = "${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION}/bin:"

# The following are for the new-style (CMake 3.8+) CUDA language
# support and to hook in our override of FindCUDA.cmake.
cmake_do_generate_toolchain_file_append_cuda() {
    cat >> ${WORKDIR}/toolchain.cmake <<EOF
set(CMAKE_CUDA_TOOLKIT_ROOT_DIR "${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION}" CACHE PATH "" FORCE)
set(CMAKE_CUDA_TOOLKIT_TARGET_DIR "${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}" CACHE PATH "" FORCE)
set(CMAKE_CUDA_TOOLKIT_INCLUDE_DIRECTORIES "\${CMAKE_CUDA_TOOLKIT_ROOT_DIR}/include" "\${CMAKE_CUDA_TOOLKIT_TARGET_DIR}/include" CACHE PATH "" FORCE)
EOF
}

meson_cuda_cross_config() {
    cat >${WORKDIR}/meson-cuda.cross <<EOF
[binaries]
cuda = 'nvcc'
EOF
}
MESON_CROSS_FILE_append_cuda_class-target = " --cross-file ${WORKDIR}/meson-cuda.cross"

PACKAGE_ARCH_cuda = "${SOC_FAMILY_PKGARCH}"
RDEPENDS_${PN}_append_tegra = " tegra-libraries"

python() {
    if bb.data.inherits_class('meson', d) and 'cuda' in d.getVar('OVERRIDES').split(':') and d.getVar('CLASSOVERRIDE') == 'class-target':
        d.appendVarFlag('do_write_config', 'postfuncs', ' meson_cuda_cross_config')
        d.setVarFlag('CUFLAGS', 'export', '1')
}
