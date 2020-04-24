require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "319771f42db1d9a4a273bc5ff753148247ece9cf7544d2008d57d9061e6964f9"
SRC_URI[dev.sha256sum] = "3bd27507b8eef4ae9d5faf8671e8843ef7bd4cede82dc76a8e6519390e22a5dc"

FILES_${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/Readme.txt \
                    ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/include ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/sample"

BBCLASSEXTEND = "native nativesdk"
