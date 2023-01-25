CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "921de34da9edfbea002e48c817fc6f178da922384ef7db16c13233263d905812"
MAINSUM:x86-64 = "87b4bd1df01cdb888df69e99f345b7f324b0bc13461af84fe6babb0450f65a52"

BBCLASSEXTEND = "native nativesdk"
