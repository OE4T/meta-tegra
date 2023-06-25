CUDA_PKG = "cuda-cupti cuda-cupti-dev"

require cuda-shared-binaries-11.8.inc

MAINSUM = "96aff8de6d070e1fc0e54777cdf2263e2ca9a95860573b45759ff4d174c8a72f"
MAINSUM:x86-64 = "7922e002784d4569902b2e0c3ac0396411e61e64a7bc4ac62c6bda678c4a590a"
DEVSUM = "6aa82f976f031333d48c48a701cff9476ab66509110c72db346be7cd3bca02b4"
DEVSUM:x86-64 = "3af21112a3a26ddd72e0dcde81f6af8973a817bdcce8d99ed06e81d6a667f9c0"

FILES:${PN}-dev += " \
    ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/Readme.txt \
    ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/include \
    ${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI/sample \
"

RDEPENDS:${PN}-dev = "perl"

BBCLASSEXTEND = "native nativesdk"
