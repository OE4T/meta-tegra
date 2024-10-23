DESCRIPTION = "NVIDIA TensorRT trtexec utility"

require tensorrt-samples.inc

EXTRA_OEMAKE =+ "samples=trtexec"

do_install() {
    install -d ${D}${prefix}/src/tensorrt/bin
    install -m 0755 ${S}/../bin/trtexec ${D}${prefix}/src/tensorrt/bin
}

FILES:${PN} += "${prefix}/src/tensorrt/bin"
