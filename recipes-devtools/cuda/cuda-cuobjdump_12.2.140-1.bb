CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "fa3b4ea649d341537fd812b13407fc0f7d67516e71136d531b533c4d07ce716b"
MAINSUM:x86-64 = "84a7790cf7f75c8737f942fcb0ff8abcec40bf090232f0860556c07fcfc68537"
BBCLASSEXTEND = "native nativesdk"
