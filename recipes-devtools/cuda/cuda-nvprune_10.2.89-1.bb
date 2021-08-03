CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

MAINSUM = "1e04820f53fb96b737c6b5d92ae2c1c54414e84a87ae9a55a00fc78c05e4e33f"
MAINSUM:x86-64 = "99fe77894938fecd6eb3172580f5c17a24686167ab222250eaa1c62e2f147372"

BBCLASSEXTEND = "native nativesdk"
