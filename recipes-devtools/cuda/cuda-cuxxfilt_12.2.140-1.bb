CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "063aa101a2adba7c9537f24aac58e513f929cfe9ab6a266425334c754bd10673"
MAINSUM:x86-64 = "dea65d67883ed7d5a51c93282d1f94fc41fed8087544ff0e33b4e7e873cff9d9"

BBCLASSEXTEND = "native nativesdk"

