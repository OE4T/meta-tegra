CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "ef5928592f8bc74eddf141f9a9dfb75c7de865985c525e12ad81a32c3832c18f"
MAINSUM:x86-64 = "902b49d21ef05d6a7fb788e2e57c26540a0d9cba26badad2d59250efb1d47c5e"

ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
