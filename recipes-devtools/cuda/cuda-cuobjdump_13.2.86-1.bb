CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "89594fd35777c75c24de6639698b708a06aa9c6288419432fe4fa71cb54943d4"
MAINSUM:x86-64 = "4bf6f16469b7e0bb60c0ec25b6d9b42360cb8c2a79dda58e9ad39dc1d048ccdc"
BBCLASSEXTEND = "native nativesdk"
