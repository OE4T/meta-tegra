CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "113b146674a3dfb7745a4e8f027030a245709a016db52f0dea2297533e92d78e"
MAINSUM:x86-64 = "ca98dd5ef3eb18a9123bf58ed6cc9e9e238f72605edb0d4054f29b1dac00b414"
DEVSUM = "7956c64970a42124e0c3f903f2504a66edf223d5130f4f4430f053aa3b9c2c9a"
DEVSUM:x86-64 = "351bfd2349ac2999042b8bb59dd9ad2d254ba4b945a050c96a028dad44033225"

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI"
RDEPENDS:${PN}-dev += "make perl perl-module-getopt-long perl-module-posix perl-module-cwd"

BBCLASSEXTEND = "native nativesdk"
