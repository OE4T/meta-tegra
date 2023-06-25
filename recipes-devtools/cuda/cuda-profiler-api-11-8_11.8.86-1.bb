CUDA_PKG = "cuda-profiler-api"

require cuda-shared-binaries-11.8.inc

MAINSUM = "a3af899c17b31d4b31c39502e0ed742c80d510071c91b4e5ba4a58f9a76cfa80"
MAINSUM:x86-64 = "755ed6c2583cb70d96d57b84082621f87f9339573b973da91faeacba60ecfeeb"

ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
