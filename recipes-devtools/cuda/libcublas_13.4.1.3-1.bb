
require cuda-shared-binaries.inc

MAINSUM = "f6b8cc5b1aac7815ebcd98852d2368559ff3811be499870f4490b3b0e6727983"
MAINSUM:x86-64 = "dfaf1cd33a9ba55a42dc0c2bb3cfaca3da97297fc947fb4d9ba9e5f5e46ef768"
DEVSUM = "45271c50b8f99fd8232e7e1bd5c7def4cc4fc32a65a8094ffa77cf66a383a9ec"
DEVSUM:x86-64 = "f3bfdb6396a6285e9c784ed9c33209c690df9b0ea5f521f3341b9b9d6e433f4e"

EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS:${PN}-stubs = "libcublas.so.13 libcublasLt.so.13 libnvblas.so.13"

BBCLASSEXTEND = "native nativesdk"
