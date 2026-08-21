
require cuda-shared-binaries.inc

MAINSUM = "532439f73935b2c92247ddeb2197e511f446d36592aa6e96f016544871275eec"
MAINSUM:x86-64 = "e4ccc032300a6b659b0d9045a2d0cc3d5f7e8a1e8c892162cf208a109ee62264"
DEVSUM = "2135ec5f8f055ef7b9f00c43718b817eab9cb7ab14dc8e116f3c1d8a7d6cca0f"
DEVSUM:x86-64 = "507708bb93ff9564e8ae3edd32d56ab045ef947139b8b4e3c31daf25ea260f72"

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI"
RDEPENDS:${PN}-dev += "make perl perl-module-getopt-long perl-module-posix perl-module-cwd"

BBCLASSEXTEND = "native nativesdk"
