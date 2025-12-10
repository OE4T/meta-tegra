CUDA_PKG = "libcufft libcufft-dev"

require cuda-shared-binaries-12.9.inc

MAINSUM = "69904d1b053ab533d06af4ed422780fe416e89a55c1dd5705f941391149ae269"
MAINSUM:x86-64 = "8687decb37f34fe0fb4c07a97f56a3b6d75a8e0b12b09aebbcfc671325a82c37"
DEVSUM = "586970e61493ce4ca26df3e49b7e8c90159b9ab56bb7670813f8646032e2cf04"
DEVSUM:x86-64 = "9c6a1819a808a9fabb8dfaf7ebb4ce2f599b3bae5fd63c09dcf24508efc0b462"

BBCLASSEXTEND = "native nativesdk"
