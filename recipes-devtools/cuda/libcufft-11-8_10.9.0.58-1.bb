CUDA_PKG = "libcufft libcufft-dev"

require cuda-shared-binaries-11.8.inc

MAINSUM = "85ab0db678898c57d673dcb31a0ddb4c80fe0b7cbfa573c2db32d9095bdce292"
MAINSUM:x86-64 = "9f90b56c83b922e4c7a972b813bd29387be05c56c6c1dbce4c2c6ef459323d28"
DEVSUM = "cea090f452ffbd85828beb2807a4498b60f87b3ef81b5667f720ecd97329f254"
DEVSUM:x86-64 = "ef6a4db5e7ca7889cd1c9c68a7808d163e487cbdfe91ed10def587242909dcd3"

BBCLASSEXTEND = "native nativesdk"
