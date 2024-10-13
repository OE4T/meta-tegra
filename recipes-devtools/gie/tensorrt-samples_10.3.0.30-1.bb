DESCRIPTION = "NVIDIA TensorRT Samples for deep learning"

require tensorrt-samples.inc

do_install() {
    install -m 0755 -D -t ${D}${prefix}/src/tensorrt/bin ${S}/../bin/sample_*
    install -d ${D}${prefix}/src/tensorrt/data
    cp -R --preserve=mode,timestamps ${S}/../data/* ${D}${prefix}/src/tensorrt/data
}

FILES:${PN} += "${prefix}/src/tensorrt/bin ${prefix}/src/tensorrt/data"
