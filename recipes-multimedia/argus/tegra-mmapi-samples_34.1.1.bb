DESCRIPTION = "NVIDIA Tegra Multimedia API sample programs"

require recipes-multimedia/argus/tegra-mmapi-${PV}.inc

SRC_URI += "\
    file://0001-include-fix-jpeglib-header-inclusion.patch \
    file://0002-tools-update-GetPixel.py-to-Python-3.patch \
    file://0003-samples-classes-fix-a-data-race-in-shutting-down.patch \
    file://0006-samples-Rework-makefiles-and-rules.patch \
    file://0007-trt_inference-use-smart-pointers-during-model-conver.patch \
    file://0008-frontend-add-option-to-set-timeout.patch \
    file://0009-camera_v4l2_cuda-add-option-for-setting-max-frame-co.patch \
"

DEPENDS = "libdrm tegra-mmapi tegra-libraries-camera virtual/egl virtual/libgles1 virtual/libgles2 jpeg expat gstreamer1.0 glib-2.0 libv4l pango"

inherit pkgconfig cuda python3native features_check

PACKAGECONFIG ??= ""
PACKAGECONFIG[objdetect] = "HAVE_OPENCV=1 HAVE_TENSORRT=1,,tensorrt-core tensorrt-plugins opencv"

REQUIRED_DISTRO_FEATURES = "x11 opengl"

CXXFLAGS += "${CUDA_CXXFLAGS}"
export NVCC = "nvcc"
export GENCODE_FLAGS = "${CUDA_NVCC_ARG_FLAGS}"
EXTRA_OEMAKE = 'VERBOSE=1 ${PACKAGECONFIG_CONFARGS} NVCCFLAGS="--shared -ccbin ${CUDAHOSTCXX} ${@cuda_extract_compiler('CXX', d)[1]}"'

do_delete_headers() {
    rm -rf ${S}/include/libjpeg-8b ${S}/include/nvbuf_utils.h
}

addtask delete_headers before do_patch after do_unpack

do_compile() {
    oe_runmake all
}

do_install() {
    oe_runmake DESTDIR="${D}" install
    install -d ${D}/opt/tegra-mmapi/data
    cp -R --preserve=mode,timestamps ${S}/data/Picture ${D}/opt/tegra-mmapi/data/
    cp -R --preserve=mode,timestamps ${S}/data/Video ${D}/opt/tegra-mmapi/data/
    if ${@bb.utils.contains('PACKAGECONFIG', 'objdetect', 'true', 'false', d)}; then
        cp -R --preserve=mode,timestamps ${S}/data/Model ${D}/opt/tegra-mmapi/data/
    fi
}

FILES:${PN} += "/opt/tegra-mmapi"
RDEPENDS:${PN} += "tegra-libraries-multimedia-v4l"

