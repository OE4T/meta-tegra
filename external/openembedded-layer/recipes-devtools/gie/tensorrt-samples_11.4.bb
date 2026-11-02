DESCRIPTION = "NVIDIA TensorRT Samples for deep learning"

require tensorrt-samples.inc

SRC_URI += "https://github.com/NVIDIA/TensorRT/releases/download/v11.0/tensorrt_sample_data_20260602.zip;name=dataset;subdir=tensorrt-dataset"
SRC_URI[dataset.sha256sum] = "10f5e8d92d610839cd963e680b27190c11db6bc0f7dafcaffeb384e1de89d68f"

EXTRA_OECMAKE:append:tegra234 = " -DTRT_BUILD_ENABLE_DLA=ON"

# trtexec is built with the samples, but is packaged by tensorrt-trtexec.
do_install:append() {
    rm -f ${D}${TRT_INSTALL_PATH}/bin/trtexec
    install -d ${D}${prefix}/src/tensorrt/data
    cp -R --preserve=mode,timestamps ${UNPACKDIR}/tensorrt-dataset/* ${D}${prefix}/src/tensorrt/data
}

FILES:${PN} += "${prefix}/src/tensorrt/bin ${prefix}/src/tensorrt/data"
