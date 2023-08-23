inherit cuda-gcc
CUDA_ARCHITECTURES ??= ""
CUDA_NVCC_COMPAT_FLAGS ??= ""
CUDA_NVCC_PATH_FLAGS ??= "--include-path ${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}/include --library-path ${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}/${baselib} -Xcompiler -isystem,=${includedir}/cuda-compat-workarounds"
CUDA_NVCC_EXTRA_FLAGS ??= ""
CUDA_NVCC_FLAGS ?= "${CUDA_NVCC_ARCH_FLAGS} ${CUDA_NVCC_COMPAT_FLAGS} ${CUDA_NVCC_PATH_FLAGS} ${CUDA_NVCC_EXTRA_FLAGS}"

CUDA_CXXFLAGS = "-I${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION}/include -I=/usr/local/cuda-${CUDA_VERSION}/include"
CUDA_LDFLAGS = "\
   -L=/usr/local/cuda-${CUDA_VERSION}/${baselib} -L=/usr/local/cuda-${CUDA_VERSION}/${baselib}/stubs \
  -Wl,-rpath-link,${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}/${baselib} \
  -Wl,-rpath,/usr/local/cuda-${CUDA_VERSION}/${baselib} \
"

LDFLAGS:prepend:cuda = "${TOOLCHAIN_OPTIONS} "
LDFLAGS:append:cuda = " ${CUDA_LDFLAGS}"
DEBUG_PREFIX_MAP:remove:cuda = "-fcanon-prefix-map"

def cuda_extract_compiler(compiler, d, prefix='-Xcompiler '):
    args = d.getVar(compiler).split()
    if args[0] == "ccache":
        return args[1], ' '.join([prefix + arg for arg in args[2:]])
    return args[0], ' '.join([prefix + arg for arg in args[1:]])

export CUDAHOSTCXX = "${@cuda_extract_compiler('CXX_FOR_CUDA', d)[0]}"
export CUDAFLAGS = "${CUDA_NVCC_FLAGS} ${@cuda_extract_compiler('CXX_FOR_CUDA', d)[1]}"
OECMAKE_CUDA_COMPILER_LAUNCHER ?= "${CCACHE}"
OECMAKE_CUDA_COMPILER ?= "nvcc"
CUDA_CCACHE_COMPILERCHECK ?= "cuda-compiler-check %compiler%"

def cmake_cuda_archs(d):
    archs = d.getVar('CUDA_ARCHITECTURES')
    return archs if archs else "OFF"
OECMAKE_CUDA_ARCHITECTURES ?= "${@cmake_cuda_archs(d)}"

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
CUFLAGS = "-ccbin ${@cuda_extract_compiler('CXX_FOR_CUDA', d)[0]} ${CUDAFLAGS} ${@cuda_extract_compiler('CXX_FOR_CUDA', d)[1]} ${@cuda_meson_ldflags(d)}"

# The following are for the old-style FindCUDA.cmake module (pre-3.8)
CUDA_EXTRA_OECMAKE = '\
  -DCUDA_TOOLKIT_TARGET_DIR=${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION} \
  -DCUDA_TOOLKIT_ROOT_DIR=${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION} \
  -DCUDA_NVCC_FLAGS="${@';'.join(d.getVar('CUDA_NVCC_FLAGS').split())}" \
'
EXTRA_OECMAKE:append:cuda = " ${CUDA_EXTRA_OECMAKE}"

export CUDA_TOOLKIT_ROOT = "${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION}"
export CUDA_NVCC_EXECUTABLE = "${CUDA_TOOLKIT_ROOT}/bin/nvcc"
export CUDACXX = "${CCACHE}${CUDA_TOOLKIT_ROOT}/bin/nvcc"
export CUDA_PATH = "${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}"

CUDA_NATIVEDEPS = "cuda-compiler-native cuda-cudart-native"
CUDA_NATIVEDEPS:class-native = ""
CUDA_DEPENDS = "cuda-libraries ${CUDA_NATIVEDEPS}"

DEPENDS:append:cuda = " ${CUDA_DEPENDS} ${@'tegra-cmake-overrides' if bb.data.inherits_class('cmake', d) else ''}"
PATH:prepend:cuda = "${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION}/bin:"

# The following are for the new-style (CMake 3.8+) CUDA language
# support and to hook in our override of FindCUDA.cmake.
cmake_do_generate_toolchain_file:append:cuda() {
    cat >> ${WORKDIR}/toolchain.cmake <<EOF
set(CMAKE_CUDA_TOOLKIT_ROOT_DIR "${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION}" CACHE PATH "" FORCE)
set(CMAKE_CUDA_TOOLKIT_TARGET_DIR "${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}" CACHE PATH "" FORCE)
set(CMAKE_CUDA_TOOLKIT_INCLUDE_DIRECTORIES "\${CMAKE_CUDA_TOOLKIT_ROOT_DIR}/include" "\${CMAKE_CUDA_TOOLKIT_TARGET_DIR}/include" CACHE PATH "" FORCE)
set(CMAKE_CUDA_COMPILER ${OECMAKE_CUDA_COMPILER})
set(CMAKE_CUDA_COMPILER_LAUNCHER ${OECMAKE_CUDA_COMPILER_LAUNCHER})
set(CMAKE_CUDA_ARCHITECTURES ${OECMAKE_CUDA_ARCHITECTURES})
EOF
}

meson_cuda_cross_config() {
    cat >${WORKDIR}/meson-cuda.cross <<EOF
[binaries]
cuda = 'nvcc'
EOF
}
MESON_CROSS_FILE:append:cuda:class-target = " --cross-file ${WORKDIR}/meson-cuda.cross"

PACKAGE_ARCH:cuda = "${SOC_FAMILY_PKGARCH}"
RDEPENDS:${PN}:append:tegra = " tegra-libraries-cuda"

python() {
    if bb.data.inherits_class('meson', d) and 'cuda' in d.getVar('OVERRIDES').split(':') and d.getVar('CLASSOVERRIDE') == 'class-target':
        d.appendVarFlag('do_write_config', 'postfuncs', ' meson_cuda_cross_config')
        d.setVarFlag('CUFLAGS', 'export', '1')
    if bb.data.inherits_class('ccache', d):
        d.appendVar('DEPENDS', ' cuda-compiler-check-native')
        if (d.getVar('CCACHE_COMPILERCHECK') or '') != '':
            d.prependVar('CCACHE_COMPILERCHECK', '${CUDA_CCACHE_COMPILERCHECK} ')
}
