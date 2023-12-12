require cuda-shared-binaries.inc

MAINSUM = "2ee29d82fc73f9176b1c8126444953815042e4ae650e42fee93e432ef3f05537"
MAINSUM:x86-64 = "98f07c1d9cb41e4c9e544326e4f1e3633741cbb63210e7db9412d367a3f77f4b"
DEVSUM = "0e7c17daa866fd0fac0e0535575f04c3891bcaec18b6485dbad48dc9d721ceaf"
DEVSUM:x86-64 = "eb71963a324d343bf26ac440bd6597b13e040865f9569bb97ebf0774b9f176bc"

do_compile:prepend() {
	rm -rf ${B}/usr/local/cuda-${CUDA_VERSION}/res
}

FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/res"

BBCLASSEXTEND = "native nativesdk"
