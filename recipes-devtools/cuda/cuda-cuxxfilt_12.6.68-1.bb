CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "abdb3b1f96722e9115a699ae02b795b7d67e127fe879612cbfb65ed86ebd6605"
MAINSUM:x86-64 = "0415eec8b8e1bb20734081a0bab44e17d513224e9626936f1f6a864310e6b5bf"

BBCLASSEXTEND = "native nativesdk"
