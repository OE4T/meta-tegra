DESCRIPTION = "NVIDIA TensorRT Lean (GPU Inference Engine) for runtime inference"

require tensorrt.inc

PROVIDES += "virtual/tensorrt-runtime"

SRC_COMMON_DEBS += "\
    libnvinfer-lean10_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvinfer-lean10_${PV}+cuda13.2_arm64.deb;name=nvl;subdir=tensorrt \
    libnvinfer-lean-dev_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvinfer-lean-dev_${PV}+cuda13.2_arm64.deb;name=nvldev;subdir=tensorrt \
"

SRC_URI[nvl.sha256sum] = "a0ed45dd7fda97fd7ee67af5ee4ed5868cd12161618b1786b12122b73f34ad36"
SRC_URI[nvldev.sha256sum] = "dbeaed0a6a6779940ed0ad1423018593a71367ca005d25c13069c0393adaf1f8"

do_install() {
    install -d ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_lean.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_lean_static.a ${D}${libdir}

    ln -s libnvinfer_lean.so.${BASEVER} ${D}${libdir}/libnvinfer_lean.so
    ln -s libnvinfer_lean.so.${BASEVER} ${D}${libdir}/libnvinfer_lean.so.${MAJVER}
}

DEPENDS += "tensorrt-headers"
