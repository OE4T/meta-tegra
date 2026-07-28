CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "9273c0b7052e1f6e51406a87585e39aa3ec6bb365cf150154a3bc1865aac039f"
MAINSUM:x86-64 = "424be796b47c8c3102f5c61cce15ba916023ab733164f7d689f20b0e2d7acb06"

BBCLASSEXTEND = "native nativesdk"
