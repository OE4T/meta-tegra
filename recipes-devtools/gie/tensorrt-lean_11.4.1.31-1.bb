DESCRIPTION = "NVIDIA TensorRT Lean (GPU Inference Engine) for runtime inference"

require tensorrt.inc

PROVIDES += "virtual/tensorrt-runtime"

SRC_COMMON_DEBS += "\
    libnvinfer-lean11_${PV}+cuda13.4_arm64.deb;downloadfilename=libnvinfer-lean11_${PV}+cuda13.4_arm64.deb;name=nvl;subdir=tensorrt \
    libnvinfer-lean-dev_${PV}+cuda13.4_arm64.deb;downloadfilename=libnvinfer-lean-dev_${PV}+cuda13.4_arm64.deb;name=nvldev;subdir=tensorrt \
"

SRC_URI[nvl.sha256sum] = "5d6ef4591508002652ba26a41b4fd63633e8854d3e96df4fdc96cf70b0d27321"
SRC_URI[nvldev.sha256sum] = "3725016fa5eb3b7ac39edf70dad1660425fc436ff84917a4d7295f3e91dc6259"

do_install() {
    install -d ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_lean.so.${BASEVER} ${D}${libdir}

    ln -s libnvinfer_lean.so.${BASEVER} ${D}${libdir}/libnvinfer_lean.so
    ln -s libnvinfer_lean.so.${BASEVER} ${D}${libdir}/libnvinfer_lean.so.${MAJVER}
}

DEPENDS += "tensorrt-headers"
