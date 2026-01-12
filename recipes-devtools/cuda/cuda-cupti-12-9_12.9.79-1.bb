CUDA_PKG = "cuda-cupti cuda-cupti-dev"

require cuda-shared-binaries-12.9.inc

MAINSUM = "56ec1f2b513a2055556fb0df4e56dd3e6168fe86d8f9705096bb71e0318ece72"
MAINSUM:x86-64 = "a9b48ff6da29ff0f20cd582070ed66f36e9ae971157316bfdf780a9ac9ad6de8"
DEVSUM = "a5266bcb145c1d9f03a6036d72c505357838e3be44a91fa45c80256b55fee2be"
DEVSUM:x86-64 = "a5300e7356dbe4e70dfa8faa987febbf96b46946ca4cfb07baa9d5ea18de6bb6"

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/extras/CUPTI"
RDEPENDS:${PN}-dev += "make perl perl-module-getopt-long perl-module-posix perl-module-cwd"

BBCLASSEXTEND = "native nativesdk"
