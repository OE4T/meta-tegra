DESCRIPTION = "NVIDIA TensorRT Headers for deep learning"
LICENSE = "Apache-2.0"

require tensorrt.inc

do_install() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/usr/include/aarch64-linux-gnu/*.h ${D}${includedir}
}

RDEPENDS:${PN}-dev = ""
