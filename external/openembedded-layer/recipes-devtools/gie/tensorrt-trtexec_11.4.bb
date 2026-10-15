DESCRIPTION = "NVIDIA TensorRT trtexec utility"

require tensorrt-samples.inc

# trtexec has no build switch of its own; it is built with the samples.
do_install:append() {
    find ${D}${TRT_INSTALL_PATH}/bin -mindepth 1 ! -name trtexec -delete
}

FILES:${PN} += "${prefix}/src/tensorrt/bin"
