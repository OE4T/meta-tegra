CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "66f06aa3e458be9e75460a3f3352a50c844a35ad5249e3e7fa2147f85a368aa6"
MAINSUM:x86-64 = "179fa83bced1ee638b2dff1c2110a94c395f6838280f215a77d571c7a19485da"

BBCLASSEXTEND = "native nativesdk"
