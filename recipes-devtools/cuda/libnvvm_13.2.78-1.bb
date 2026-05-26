CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "8e82231d4658514e8456238a574feb56ff8444392f64bf48baf50a676db4ec5b"
MAINSUM:x86-64 = "282145657fc54291e4cc64a6153425b75a0326d53789b57395bf52f0ea6ae115"

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}"
FILES:${PN}-dev = ""
INSANE_SKIP:${PN} += "dev-so"

BBCLASSEXTEND = "native nativesdk"
