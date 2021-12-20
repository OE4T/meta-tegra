DESCRIPTION = "CUDA sample programs"
SRC_URI = ""
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://EULA.txt;md5=c3efeff9b7401d0a242a86d9051a8815"

COMPATIBLE_MACHINE = "(tegra)"

CUDA_VERSION_DASHED = "${@d.getVar('CUDA_VERSION').replace('.','-')}"
SRC_COMMON_DEBS = "${BPN}-${CUDA_VERSION_DASHED}_${PV}_arm64.deb;unpack=false"
L4T_DEB_GROUP = "cuda-samples"
SRC_URI[sha256sum] = "bb7726194bac9252863da80ae13e4fdd7e69a657314cb8ff6edf8ba1cd789e2d"

do_unpack_samples() {
    dpkg-deb --fsys-tarfile ${WORKDIR}/cuda-samples-10-2_${PV}_arm64.deb | \
        tar --strip-components=5 --exclude="*/doc/*" --exclude="*/bin/*" -x -f- -C ${S}
}

do_unpack_samples[dirs] = "${S}"
do_unpack_samples[cleandirs] = "${S}"
do_unpack_samples[depends] += "dpkg-native:do_populate_sysroot"

addtask unpack_samples after do_unpack before do_patch

inherit l4t_deb_pkgfeed cuda

CUDA_NVCC_ARCH_FLAGS ??= ""

def extract_sm(d):
    archflags = d.getVar('CUDA_NVCC_ARCH_FLAGS').split()
    for flag in archflags:
        parts = flag.split('=')
        if len(parts) == 2 and parts[0] == '--gpu-code':
            return parts[1].split('_')[1]
    return ''

CUDA_SAMPLES ?= " \
    0_Simple/UnifiedMemoryStreams \
    1_Utilities/deviceQuery \
"

S = "${WORKDIR}/${BP}"
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
    CUDA_PATH="${STAGING_DIR_HOST}/${CUDA_PATH}" HOST_COMPILER="${CC_FIRST}" CCFLAGS="${CC_REST} ${CFLAGS}" LDFLAGS="${LINKFLAGS}" \
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
        install -m 0755 "$f" ${D}${bindir}/cuda-samples/
    done
    dpkg-deb --fsys-tarfile ${WORKDIR}/cuda-samples-10-2_${PV}_arm64.deb | \
        tar --exclude="*usr/share*" -x -f- -C ${D}
}

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
FILES:${PN} = "${bindir}/cuda-samples"
FILES:${PN}-dev = "${CUDA_PATH}"
INSANE_SKIP:${PN}-dev = "staticdev"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
