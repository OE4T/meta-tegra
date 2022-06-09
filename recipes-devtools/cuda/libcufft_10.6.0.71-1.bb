require cuda-shared-binaries.inc

MAINSUM = "b07e5e18c7cd2d3759eba6b20a1d01709aa956b5f1ca415f0a667d34889837b0"
MAINSUM:x86-64 = "6063dd3f500394099049bb731da92e0400fe188d5186b1c7c4de4a023874f221"
DEVSUM = "3e483181d4864f70ca25f5856368d9dedd90fec82da500441368a394b0baada5"
DEVSUM:x86-64 = "f2b5e67fa4e749e6e5b22688e5afa57817e752a1182c09646603051f695d6b36"

BBCLASSEXTEND = "native nativesdk"

PACKAGES =+ "${PN}-devso-symlink"
FILES:${PN}-devso-symlink = "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libcufft${SOLIBSDEV}"
RDEPENDS:${PN}-devso-symlink = "${PN}"
INSANE_SKIP:${PN}-devso-symlink = "dev-so libdir"
RDEPENDS:${PN}-dev += "${PN}-devso-symlink"
RPROVIDES:${PN}-devso-symlink += "libcufft.so()(64bit) cufft libcufft.so"
