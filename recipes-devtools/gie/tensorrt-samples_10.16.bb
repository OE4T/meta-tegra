DESCRIPTION = "NVIDIA TensorRT Samples for deep learning"

require tensorrt-samples.inc

DEPENDS += "unzip-native"

SRC_URI += "https://github.com/NVIDIA/TensorRT/releases/download/v10.15/tensorrt_sample_data_20260203.zip;name=dataset"
SRC_URI[dataset.sha256sum] = "7faf01ef2f5debe849b8a535516135ce1dcf38fddfa5cf18a1773a06ebc56578"

EXTRA_OECMAKE:append = " \
    -DBUILD_SAMPLES=ON \
    -DBUILD_TRTEXEC=OFF \
"

do_configure:append() {
    install -d ${UNPACKDIR}/tensorrt-dataset
    unzip ${UNPACKDIR}/tensorrt_sample_data_20260203.zip -d ${UNPACKDIR}/tensorrt-dataset
}
do_configure[cleandirs] += "${UNPACKDIR}/tensorrt-dataset"

do_install:append() {
    install -d ${D}${prefix}/src/tensorrt/data
    cp -R --preserve=mode,timestamps ${UNPACKDIR}/tensorrt-dataset/tensorrt_sample_data_20260203/* ${D}${prefix}/src/tensorrt/data
}

FILES:${PN} += "${prefix}/src/tensorrt/bin ${prefix}/src/tensorrt/data"
