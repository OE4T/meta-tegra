DESCRIPTION = "CUDA sample programs"
HOMEPAGE = "https://github.com/NVIDIA/cuda-samples"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bb28b97ff25ae39de442985ec577dbd8"

COMPATIBLE_MACHINE = "(tegra)"

SRC_URI = "git://github.com/NVIDIA/cuda-samples.git;protocol=https;branch=master"
# v12.5 tag
SRCREV = "9c688d7ff78455ed42e345124d1495aad6bf66de"

inherit cuda

DEPENDS:append = "cuda-crt"

PV = "12.5"
CUDA_NVCC_ARCH_FLAGS ??= ""

def extract_sm(d):
    archflags = d.getVar('CUDA_NVCC_ARCH_FLAGS').split()
    for flag in archflags:
        parts = flag.split('=')
        if len(parts) == 2 and parts[0] == '--gpu-code':
            return parts[1].split('_')[1]
    return ''

CUDA_SAMPLES ?= " \
    Samples/0_Introduction/UnifiedMemoryStreams \
    Samples/1_Utilities/deviceQuery \
"

S = "${WORKDIR}/git"
B = "${S}"

CUDA_PATH = "/usr/local/cuda-${CUDA_VERSION}"
CC_FIRST = "${@cuda_extract_compiler('CC_FOR_CUDA', d)[0]}"
CC_REST = "${@cuda_extract_compiler('CC_FOR_CUDA', d, prefix='')[1]}"
CFLAGS += "-I=${CUDA_PATH}/include"
EXTRA_NVCCFLAGS = "-I${STAGING_DIR_HOST}${CUDA_PATH}/include"

def filtered_ldflags(d):
    newflags = []
    for flag in d.getVar('LDFLAGS').split():
        if flag.startswith('-f'):
            continue
        if flag.startswith('-Wl,'):
            newflags.append(flag[4:])
        else:
            newflags.append(flag)
    return ' '.join(newflags)

LINKFLAGS = "-L${STAGING_DIR_HOST}${CUDA_PATH}/${baselib} ${TOOLCHAIN_OPTIONS} ${@filtered_ldflags(d)} -lstdc++"

EXTRA_OEMAKE = ' \
    GENCODE_FLAGS="${CUDA_NVCC_ARCH_FLAGS}" SMS="${@extract_sm(d)}" OPENMP=yes \
    CUDA_PATH="${STAGING_DIR_HOST}/${CUDA_PATH}" HOST_COMPILER="${CC}" CCFLAGS="${CC_REST} ${CFLAGS}" LDFLAGS="${LINKFLAGS}" \
    TARGET_ARCH="${TARGET_ARCH}" TARGET_OS="${TARGET_OS}" HOST_ARCH="${BUILD_ARCH}" \
    NVCC="${STAGING_DIR_NATIVE}/${CUDA_PATH}/bin/nvcc -ccbin ${CC_FIRST} ${CUDA_NVCC_PATH_FLAGS}" \
'

do_configure() {
    oldwd="$PWD"
    for subdir in ${CUDA_SAMPLES}; do
        cd "$subdir"
        oe_runmake clean
        cd "$oldwd"
    done
}

do_compile() {
    oldwd="$PWD"
    for subdir in ${CUDA_SAMPLES}; do
        cd "$subdir"
        oe_runmake
        cd "$oldwd"
    done
}

do_install() {
    install -d ${D}${bindir}/cuda-samples
    for f in ${B}/bin/${TARGET_ARCH}/${TARGET_OS}/release/*; do
        [ -e "$f" ] || continue
        install -m 0755 "$f" ${D}${bindir}/cuda-samples
    done
}

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
FILES:${PN} = "${bindir}/cuda-samples"
FILES:${PN}-dev = "${CUDA_PATH}"
INSANE_SKIP:${PN}-dev = "staticdev"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
