CUDA_PKG = "libnpp libnpp-dev"

require cuda-shared-binaries-11.8.inc

MAINSUM = "fd1fb2818b87d7e4c4fb8925ac2208c22362ba8b48f26b1779f2cddfd4f5abfa"
MAINSUM:x86-64 = "6f75e8540f50dd3461ccc2efa6352b80c81a30de7e43ffa703cb8f044d8bfcd3"
DEVSUM = "2f3132e7fe90265244a9b4441cabbcb47ed6bdc70ccce10eb07a01de1322acae"
DEVSUM:x86-64 = "1e7d6cde014143f9eeb6d5e6383f6cca301e3746af5409e2d072bf35ccff8ff4"

BBCLASSEXTEND = "native nativesdk"
