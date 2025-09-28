CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "99811d486c94dd7872da2d08bf75ff15692a83aba20d9acd7b664b48994281f4"
MAINSUM:x86-64 = "8d57af47bf92e7b7142e410179d141ba83ccd9c02fc1c2117b1d8b5b973434d9"
DEVSUM = "eca54fc2cba92e7bef84df0d4ed98b3b2c5d99d3a9c8f28a2895b68de520a0b9"
DEVSUM:x86-64 = "21c38b76cabe7ec221b84a8481aea24a883140a860722bda93816b65ecbb6834"

EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS:${PN}-stubs = "libcublas.so.13 libcublasLt.so.13 libnvblas.so.13"

BBCLASSEXTEND = "native nativesdk"
