CUDA_PKG = "libcusolver libcusolver-dev"

require cuda-shared-binaries-12.9.inc

MAINSUM = "1bc1c78880cec6bbf30e1ee912bbaeef4b18fee0a9c330f57a5354cafc9fc54f"
MAINSUM:x86-64 = "75f819430e2fd1bbed16329993c0f270d8c970f65f0c9c15d31ed8b6c2b5ae14"
DEVSUM = "c4091a6a2d3696ab86b54489107da108f88ec7aa86e146879e5c5855f11a0da7"
DEVSUM:x86-64 = "bc85bdce2788f135b6f751fc97a50bcc0958817ba649fc66dbe8d24fed6dbf49"

RDEPENDS:${PN} = "libcublas-12-9 libcusparse-12-9 libnvjitlink-12-9"
BBCLASSEXTEND = "native nativesdk"
