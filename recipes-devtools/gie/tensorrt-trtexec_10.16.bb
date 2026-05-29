DESCRIPTION = "NVIDIA TensorRT trtexec utility"

require tensorrt-samples.inc

EXTRA_OECMAKE:append = " \
    -DBUILD_SAMPLES=OFF \
    -DBUILD_TRTEXEC=ON \
"

FILES:${PN} += "${prefix}/src/tensorrt/bin"
