CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "8e82231d4658514e8456238a574feb56ff8444392f64bf48baf50a676db4ec5b"
MAINSUM:x86-64 = "162f2119cc2dbd19b686c075215d62c2b9db8cff0390937a3a2d7b0d9334181e"

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}"
FILES:${PN}-dev = ""
INSANE_SKIP:${PN} += "dev-so"

BBCLASSEXTEND = "native nativesdk"
