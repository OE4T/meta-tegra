CUDA_PKG = "libcublas libcublas-dev"

require cuda-shared-binaries-12.9.inc

MAINSUM = "d42a77917a867c7fadb96cbe67f5469e75712912ff35019319e84fc42aa0591b"
MAINSUM:x86-64 = "e20d6c5e1e8b05c1b7ca25bffa0b60eac213a19aa4780adbdc0ec254e8071f9f"
DEVSUM = "3c4de4aead5eb45999d513e7ed45436f386001c3094113ea6b100a89343c074c"
DEVSUM:x86-64 = "7f09cc1eccfa2da100f4cfdafcaa5e978ef0972ab1840c2436b68ee83e7069b5"

EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS:${PN}-stubs = "libcublas.so.12 libcublasLt.so.12"

BBCLASSEXTEND = "native nativesdk"
