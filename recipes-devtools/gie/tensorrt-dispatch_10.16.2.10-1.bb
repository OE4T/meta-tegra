DESCRIPTION = "NVIDIA TensorRT Dispatch Library (GPU Inference Engine) for runtime inference"

require tensorrt.inc

SRC_COMMON_DEBS += "\
    libnvinfer-dispatch10_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvinfer-dispatch10_${PV}+cuda13.2_arm64.deb;name=disp;subdir=tensorrt \
    libnvinfer-dispatch-dev_${PV}+cuda13.2_arm64.deb;downloadfilename=libnvinfer-dispatch-dev_${PV}+cuda13.2_arm64.deb;name=dispdev;subdir=tensorrt \
"

SRC_URI[disp.sha256sum] = "c5812d5f1fa4036a3c2710c53722ed96e87885a4f615f528cc45a1e00b7c28fc"
SRC_URI[dispdev.sha256sum] = "2411eec858d7cac59e37433a449f19663b61e4a26c6c11868a769e34817f8c8b"

do_install() {
    install -d ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_dispatch.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_dispatch_static.a ${D}${libdir}

    ln -s libnvinfer_dispatch.so.${BASEVER} ${D}${libdir}/libnvinfer_dispatch.so
    ln -s libnvinfer_dispatch.so.${BASEVER} ${D}${libdir}/libnvinfer_dispatch.so.${MAJVER}
}

DEPENDS += "tensorrt-headers"
