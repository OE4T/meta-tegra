CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "5a001e6af583fea15af01654bab8fecae14c5fa5fb3039a92a8df2253f565af4"
MAINSUM:x86-64 = "c18e92b0ce5065629753aaf60defc8d2136f9ab05f05d26cdf3dfbb253211a7a"

BBCLASSEXTEND = "native nativesdk"
