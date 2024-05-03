require cuda-shared-binaries.inc

MAINSUM = "67f4840d329fda34f2f73345abb1e982215250247a831f07201225c6a6bbe753"
MAINSUM:x86-64 = "c7b6e974acca923854b46830621640ceb859bc09aac2cc608eb6bec83d4238f5"
DEVSUM = "512b41b895043b0abf88570824d2831219ea50395ff31fe31171ae62a7b7e06a"
DEVSUM:x86-64 = "56d1dee0937011377f498a44930ba9019534e0180a7b9ff0ed793ce9d5c783ec"

do_install:append() {
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/gds
}

BBCLASSEXTEND = "native nativesdk"
