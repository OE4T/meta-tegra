
require cuda-shared-binaries.inc

MAINSUM = "6546e1ae6cafbbd21233831ac232769380419424e099fbb47b6c30b63c3614a1"
MAINSUM:x86-64 = "88b3ee2c3c939ddd32e1078f6ed63d1b3acf7ad927b3006b55c884c94557d2e5"
DEVSUM = "eb1aa4d25003af4fe176dfa797a894afd9d30db07ec270218695d318af5c2050"
DEVSUM:x86-64 = "11350d2c92d3344afe1765a6a76b085c78e1b02d985384ee7e56c1957e0c7bcd"

do_compile:prepend() {
	rm -rf ${B}/usr/local/cuda-${CUDA_VERSION}/res
}

FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/res"

BBCLASSEXTEND = "native nativesdk"
