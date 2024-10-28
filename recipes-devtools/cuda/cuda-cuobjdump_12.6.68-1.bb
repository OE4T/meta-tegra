CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "906f876946be37bd67bad65b2a01aad5b1f63e85a1547f13d19c547a8e8ebc10"
MAINSUM:x86-64 = "73c9ccda8a4820f80f8653e952af45bfc9078e2ed43517ee6784e7f33dc4723e"
BBCLASSEXTEND = "native nativesdk"
