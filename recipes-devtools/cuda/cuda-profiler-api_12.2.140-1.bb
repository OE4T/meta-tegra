CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "00cf0777b62c908c778abbc0d52b255ca28a26695e4dcf2b9b8e75331f6c1a60"
MAINSUM:x86-64 = "b795cacbad988234eafc4ad2603925e89bdb7e00a2793c6d6e3d8d6589637861"

ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
