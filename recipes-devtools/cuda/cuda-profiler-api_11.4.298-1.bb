CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "9bb846ce5fee0fe6617e9329aee8a47d813cbd397f66edb972069101ac3365ce"
MAINSUM:x86-64 = "ac62d8ff4e7be3103766406e6420cdccbff13afdd5a43d8755c7e29281d3ae66"

ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
