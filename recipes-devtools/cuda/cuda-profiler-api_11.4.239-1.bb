CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "9bb846ce5fee0fe6617e9329aee8a47d813cbd397f66edb972069101ac3365ce"
MAINSUM:x86-64 = "4757be97a35c4d16a82ce696b8686b44299e883b51d8ee8c3057a7010017d8a7"

ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
