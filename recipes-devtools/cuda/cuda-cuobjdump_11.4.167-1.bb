CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "4347fb701b537664af1a0e0e53dc6335c7de5204f510a285a62d98fe1af4f1cc"
MAINSUM:x86-64 = "926c8cd1d36e7a87f8cf86bffd2d1a75871f146159f4a3e1671d10073f0cf659"
BBCLASSEXTEND = "native nativesdk"
