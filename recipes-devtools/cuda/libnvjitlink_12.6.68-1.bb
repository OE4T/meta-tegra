require cuda-shared-binaries.inc

MAINSUM = "98c811d3fa81a1e55cc6c4fe3dc4fa0cb3b10a26b7ed23341418333b65b832a0"
MAINSUM:x86-64 = "a97bbe1de7dfd58401583ba1e492f3807c105896f876a90d613c166bae866057"
DEVSUM = "c97e77cef8833c13207ddaed119201434b643267be7c08f7e7f735467f7ec15b"
DEVSUM:x86-64 = "eb71963a324d343bf26ac440bd6597b13e040865f9569bb97ebf0774b9f176bc"

do_compile:prepend() {
	rm -rf ${B}/usr/local/cuda-${CUDA_VERSION}/res
}

FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/res"

BBCLASSEXTEND = "native nativesdk"
