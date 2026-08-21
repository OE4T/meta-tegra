CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "128624a95cdf39a4e0517ef25b0a7132bfff3393f66642ebfb46a7425816757a"
MAINSUM:x86-64 = "8af84bfc99079ca082f0192c087fb2fd42acb02b558f64a3376e83cb2f1065cf"

BBCLASSEXTEND = "native nativesdk"
