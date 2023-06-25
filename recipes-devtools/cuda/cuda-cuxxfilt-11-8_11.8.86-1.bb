CUDA_PKG="cuda-cuxxfilt"

require cuda-shared-binaries-11.8.inc

MAINSUM = "dc5dc0cc400fa6b6e485995a12c7c4702e0c941f6dfe4c442f48c02f35a8c614"
MAINSUM:x86-64 = "5b5130f758b92ad03e617cd353752997aa33538c80fb6aa6de106e12c8fad808"

BBCLASSEXTEND = "native nativesdk"
