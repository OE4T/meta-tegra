DESCRIPTION = "NVIDIA Video Codec SDK headers and sample applications"
HOMEPAGE = "https://developer.nvidia.com/video-codec-sdk"
LICENSE = "LicenseRef-Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/${BPN}/copyright;md5=b51ea6591c7d5bdca51ff0e8ed4b333e"

inherit l4t_deb_pkgfeed cmake pkgconfig cuda

SRC_COMMON_DEBS = "${BPN}_${PV}-2_arm64.deb;subdir=${BPN}"
SRC_URI[sha256sum] = "63047d99b1b65d472b48dac2c821d26a32c102a42c2e20c8df499b1194216c36"

SRC_URI += "file://0001-samples-honour-CMAKE_CUDA_ARCHITECTURES.patch"

S = "${UNPACKDIR}/${BPN}"

SDKDIR = "/opt/nvidia/video-codec-sdk/${PV}"
OECMAKE_SOURCEPATH = "${S}${SDKDIR}/Samples"

COMPATIBLE_MACHINE = "(tegra264)"

DEPENDS = "tegra-libraries-cuda cuda-cudart cuda-crt cuda-profiler-api"

# The decode and transcode samples need ffmpeg, which is commercially licensed.
PACKAGECONFIG ??= "\
    ${@bb.utils.filter('DISTRO_FEATURES', 'vulkan', d)} \
    ${@bb.utils.contains_any('LICENSE_FLAGS_ACCEPTED', 'commercial commercial_ffmpeg', 'ffmpeg', '', d)} \
"
PACKAGECONFIG[ffmpeg] = ",,ffmpeg"
PACKAGECONFIG[vulkan] = "-DCMAKE_DISABLE_FIND_PACKAGE_Vulkan=OFF,-DCMAKE_DISABLE_FIND_PACKAGE_Vulkan=ON,vulkan-loader vulkan-headers"

# The samples link the SDK's aarch64 stubs by path, which would put the
# build tree in their RUNPATH; the libraries they load are all on the
# default search path.
EXTRA_OECMAKE = "-DSKIP_GL_DEPENDENCY=TRUE -DCMAKE_SKIP_RPATH=ON"
CXXFLAGS += "${CUDA_CXXFLAGS}"

SAMPLES_FFMPEG = "\
    AppEncDec AppTrans AppTransOneToN AppTransPerf \
    AppDec AppDecImageProvider AppDecLowLatency AppDecMem AppDecMultiFiles \
    AppDecMultiInput AppDecPerf \
"
SAMPLES = "\
    AppEncCuda AppEncLowLatency AppEncME AppEncPerf AppEncMultiInstance AppEncQual \
    ${@bb.utils.contains('PACKAGECONFIG', 'ffmpeg', '${SAMPLES_FFMPEG}', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'vulkan', 'AppMotionEstimationVkCuda', '', d)} \
"

do_install() {
    install -d ${D}${includedir}
    install -m 0644 ${S}${SDKDIR}/Interface/*.h ${D}${includedir}/

    install -d ${D}${bindir}/${BPN}-samples
    for app in ${SAMPLES}; do
        install -m 0755 ${B}/*/$app/$app ${D}${bindir}/${BPN}-samples/
    done
}

PACKAGES =+ "${PN}-samples"
ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-samples = "${bindir}/${BPN}-samples"
# nvlink records its command line, including sysroot paths, in the device code
INSANE_SKIP:${PN}-samples += "buildpaths"

RDEPENDS:${PN}-dev += "cuda-cudart-dev"
RDEPENDS:${PN}-samples += "tegra-libraries-video-codec"
