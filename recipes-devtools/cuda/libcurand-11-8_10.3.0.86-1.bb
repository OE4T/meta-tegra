CUDA_PKG = "libcurand libcurand-dev"

require cuda-shared-binaries-11.8.inc

MAINSUM = "97c8eaede35764ad739418e93e09c537252f8e0b5e74d0dd1dbf89ba6f3c815a"
MAINSUM:x86-64 = "1af8f0163cf37874fd7f4c4ed2e0f77e6676badda2572773c559c0a42e371dde"
DEVSUM = "15faf8bc98e26f9a3f313f4bfccf3a615abe633380e60c87460fb3b77002f53d"
DEVSUM:x86-64 = "e94801f76bb6af2c09f9d4166fb33478d08ae205355f3f2d7ae01baff9320ff9"

BBCLASSEXTEND = "native nativesdk"
