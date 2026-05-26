CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "0c8307645d5b45373f35fb2fb46b631adcf3a712bd49a7fd69d2bbf0157efbc7"
MAINSUM:x86-64 = "658d173cfb6f03b5afab88ad6c0d2a4c9f4e5c7b122c60168220845794c10ec3"

BBCLASSEXTEND = "native nativesdk"
