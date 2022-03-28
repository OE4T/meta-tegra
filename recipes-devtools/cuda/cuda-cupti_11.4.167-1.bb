require cuda-shared-binaries.inc

MAINSUM = "2bd620573e5c39b9c64a7aec3df15a0d9822ba8061b594ef8cb8e0efc9e64604"
MAINSUM:x86-64 = "74af753f86b7042232eb739b83cf90fe6734759cf8fb7291b234ea024fcee31e"
DEVSUM = "aa0ea3113a895cd1c0652220eadf9ec583aaa52ce4ef8928163de10852625e04"
DEVSUM:x86-64 = "3adbe5593624a069a0f9feb272144cd008194c6bde58fbbfbefccff4e3db7800"

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/Readme.txt \
                    ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/include ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/sample"

BBCLASSEXTEND = "native nativesdk"
