require cuda-shared-binaries-${PV}.inc

MAINSUM = "e2024cd43668e3102c6afd0767cfcbc107e49b5198fbb437591758b0c2c4f6f1"
MAINSUM:x86-64 = "9db8977edb2d5099bb12b5b695f1a119394f165444d4fc6bc079d217eaa229b4"
DEVSUM = "731abaaf4c5ce24c4a4ecce6b1921b88662b70e57bffee0b20bbee9b17fc4353"
DEVSUM:x86-64 = "7e07c4c570892795583e5999b5daae848aa0afdcb9963cd5f9cd62c707b48141"

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/Readme.txt \
                    ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/include ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/sample"

BBCLASSEXTEND = "native nativesdk"
