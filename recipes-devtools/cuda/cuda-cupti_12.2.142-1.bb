require cuda-shared-binaries.inc

MAINSUM = "ad15f87e8e137447cb91ea4d5060a91a854fc63cbd96ef5e13c937cf84b44a99"
MAINSUM:x86-64 = "d8405f1e1543ef2871a257bbbe46ede8f834585474870380560a34205463105b"
DEVSUM = "57d9060c5b48869661a665f37a0d44bc5dd876f68bf1adf85e2646b3f29263a7"
DEVSUM:x86-64 = "4401ebaf6222a2d409dc16b3b8b2272c9807a94363aae4af4be0d4a98c247c33"

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/Readme.txt \
                    ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/include ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/sample"

BBCLASSEXTEND = "native nativesdk"
