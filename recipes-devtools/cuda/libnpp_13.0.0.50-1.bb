CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "9fa1c60a853e410d145cb2e1ca8141bd4efaf26ecda8c5631f30f175195b7102"
MAINSUM:x86-64 = "c3ae622c9737c9c8bf9ba854e94d6a5c4fb001c58eada464289dd5ff5298ae98"
DEVSUM = "85b88ce06a101224ebe7be293f38df25e7daf507e962bbb098e1a68e212501f7"
DEVSUM:x86-64 = "3f275045f2a70a9fa87d8e8ac04ecfaea5d58ab3973630c3bdc9e88f837b345b"

BBCLASSEXTEND = "native nativesdk"
