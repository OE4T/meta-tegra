DESCRIPTION = "NVIDIA TensorRT Samples for deep learning"

require tensorrt-samples.inc

do_install() {
    install -d ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_algorithm_selector ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_char_rnn ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_dynamic_reshape ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_fasterRCNN ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_googlenet ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_int8 ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_int8_api ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_io_formats ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_mnist ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_mnist_api ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_onnx_mnist ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_ssd ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_uff_faster_rcnn ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_uff_mask_rcnn ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_uff_mnist ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_uff_plugin_v2_ext ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/sample_uff_ssd ${D}${prefix}/src/tensorrt/bin

    install -d ${D}${prefix}/src/tensorrt/data
    cp -R --preserve=mode,timestamps ${S}/../data/* ${D}${prefix}/src/tensorrt/data
}

FILES:${PN} += "${prefix}/src/tensorrt/bin ${prefix}/src/tensorrt/data"
