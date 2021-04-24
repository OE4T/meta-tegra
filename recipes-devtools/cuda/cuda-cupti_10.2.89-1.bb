require cuda-shared-binaries-${PV}.inc

MAINSUM = "319771f42db1d9a4a273bc5ff753148247ece9cf7544d2008d57d9061e6964f9"
MAINSUM_x86-64 = "5e431c6f9da2b11d9b64fe5e8e1bd6d9d61759b275f7835f3cf546a985c2b440"
DEVSUM = "3bd27507b8eef4ae9d5faf8671e8843ef7bd4cede82dc76a8e6519390e22a5dc"
DEVSUM_x86-64 = "511439b3f21837d3ba42862f33b44fbec44ceba23abff1e687126981a07d62e2"

FILES_${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/Readme.txt \
                    ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/include ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/sample"

BBCLASSEXTEND = "native nativesdk"
