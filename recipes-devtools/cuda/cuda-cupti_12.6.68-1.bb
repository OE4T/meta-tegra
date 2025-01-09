require cuda-shared-binaries.inc

MAINSUM = "4458ad07170a0a7f8a8d5864dfa0936bac767d7878a3cd753c3cf50dc1ff631a"
MAINSUM:x86-64 = "2d4cf8e7bfc8510057bb73232beb11fa8c443d88329c4b8eb3178faf7a0f4cfa"
DEVSUM = "aa44c4e149b4ee239a09c03c1fe3617b73157716d6820e2345b8244a3a8fb0d5"
DEVSUM:x86-64 = "6dd2bf16addd2fccd9fc19ede7c19bc7a1946baac02bf8d98cd648641900da11"

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI"
RDEPENDS:${PN}-dev += "make perl perl-module-getopt-long perl-module-posix perl-module-cwd"

BBCLASSEXTEND = "native nativesdk"
