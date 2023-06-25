CUDA_PKG = "libcublas libcublas-dev"

require cuda-shared-binaries-11.8.inc

MAINSUM = "2a433d986df2e276bccc544bcefa56b8ef1a46f5b4c73ce5aacc6cf6c261067f"
MAINSUM:x86-64 = "fc7c7c6b5faba8410e884ba27759c0f9cacb69432e4e9254c05fc55f4ccfa4d9"
DEVSUM = "42f110aa2f42bb06270542be236170a90d7693805cdde64ca87320e7d2efb4d0"
DEVSUM:x86-64 = "84616862f8d45a9042bd6ea584440e2b6a6f5615c267ad2d25ab63f2a51d2428"

BBCLASSEXTEND = "native nativesdk"
