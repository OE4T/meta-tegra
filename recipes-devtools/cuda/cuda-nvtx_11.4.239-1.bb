CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "b8669c8e819053d7c78c13c374b86871b131cc233d7c240d1b4c4d21c0369f6f"
MAINSUM:x86-64 = "6c63a2e71565dc7e5047945942cca5bb83bb4a998df20715bbd2a91df5afcc71"

BBCLASSEXTEND = "native nativesdk"
