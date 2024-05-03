CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "b61752a19b6e016cfc1eb10e5a64b8c8e624861f41a3dc4a37183034e8cb1006"
MAINSUM:x86-64 = "e104fc3d4288de50c92b1e25e8eff1f3781cebe46abf1b909017c06aa5bf8585"

FILES:${PN} = " \
    ${prefix}/local/cuda-${CUDA_VERSION}/include \
    ${prefix}/local/cuda-${CUDA_VERSION}/lib \
"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
