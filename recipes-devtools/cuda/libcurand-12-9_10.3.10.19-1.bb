CUDA_PKG = "libcurand libcurand-dev"

require cuda-shared-binaries-12.9.inc

MAINSUM = "d1d2ea91ace274484a7712afc2c55950d8c5e1f423ae66fa9ea346fe55307d65"
MAINSUM:x86-64 = "745bdfe7fc3651202451fe1f11b5be647d00a4bbd31ba4058720cec39967e6f0"
DEVSUM = "da7d85da4a2f1024721187a522771bf805bdd59a8f2e1ea11ff8762122d2be4f"
DEVSUM:x86-64 = "ffa9e0661c697f5d0a890bdf8d80783e0bf96d2adb2c5a564e9d8b27d095546f"

BBCLASSEXTEND = "native nativesdk"
