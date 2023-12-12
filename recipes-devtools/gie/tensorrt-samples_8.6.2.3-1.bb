DESCRIPTION = "NVIDIA TensorRT Samples for deep learning"

require tensorrt-samples.inc

do_install() {
    install -d ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_algorithm_selector ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_char_rnn ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_dynamic_reshape ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_int8_api ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_io_formats ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_onnx_mnist ${D}${prefix}/src/tensorrt/bin

    install -d ${D}${prefix}/src/tensorrt/data
    cp -R --preserve=mode,timestamps ${S}/../data/* ${D}${prefix}/src/tensorrt/data
}

FILES:${PN} += "${prefix}/src/tensorrt/bin ${prefix}/src/tensorrt/data"
