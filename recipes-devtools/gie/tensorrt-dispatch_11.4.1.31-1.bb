DESCRIPTION = "NVIDIA TensorRT Dispatch Library (GPU Inference Engine) for runtime inference"

require tensorrt.inc

SRC_COMMON_DEBS += "\
    libnvinfer-dispatch11_${PV}+cuda13.4_arm64.deb;downloadfilename=libnvinfer-dispatch11_${PV}+cuda13.4_arm64.deb;name=disp;subdir=tensorrt \
    libnvinfer-dispatch-dev_${PV}+cuda13.4_arm64.deb;downloadfilename=libnvinfer-dispatch-dev_${PV}+cuda13.4_arm64.deb;name=dispdev;subdir=tensorrt \
"

SRC_URI[disp.sha256sum] = "6810e1591fabe996805c42e5060132cdcfc8e83ac8019c026b7470610ef9512c"
SRC_URI[dispdev.sha256sum] = "55c0b8ab4fd02b68598eb5355817d99b39549d786686cf7a184b9d99265ec2d8"

do_install() {
    install -d ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_dispatch.so.${BASEVER} ${D}${libdir}

    ln -s libnvinfer_dispatch.so.${BASEVER} ${D}${libdir}/libnvinfer_dispatch.so
    ln -s libnvinfer_dispatch.so.${BASEVER} ${D}${libdir}/libnvinfer_dispatch.so.${MAJVER}
}

DEPENDS += "tensorrt-headers"
