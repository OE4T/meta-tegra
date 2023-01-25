require cuda-shared-binaries.inc

MAINSUM = "931a5a27ec2dee4c9a1c9f8bb6125af320f0752b4bbc7865dd4f6b578f55842a"
MAINSUM:x86-64 = "94bd31392f61531faf6586eaef9d56c432ee33d6088d540aa6d073113cee5604"
DEVSUM = "10b120cf4ae112f071134bc3541c5e4ee77d756f4729b330652044c8949e942b"
DEVSUM:x86-64 = "bfe08af693bdd857be1b4f0f4110aaf7814fa086d713453f3bede5eda5d124a1"

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/Readme.txt \
                    ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/include ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/sample"

BBCLASSEXTEND = "native nativesdk"
