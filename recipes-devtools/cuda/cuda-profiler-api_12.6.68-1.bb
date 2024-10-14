CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "4e695d6e559f453c3e637e594d98c7b8b605a6816920ec0e5f501eda7e32818f"
MAINSUM:x86-64 = "a45b9de833315133dd442d73b57a9e88a18f5d02c7468e514cf89168eceb72eb"

ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
