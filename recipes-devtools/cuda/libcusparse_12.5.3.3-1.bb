require cuda-shared-binaries.inc

MAINSUM = "c665700c6aae6a4aace16a7b1ff833fd73850a6fdafb41ed04f2e328a2769b9b"
MAINSUM:x86-64 = "dd9377a32bc4a236a3626ac91156e7e1b661ea48bce5aafc5dbd0a91c5e892a0"
DEVSUM = "e7f4a85c4fcd99c236be4c336db1a6f985168a98bd8f1e94609e8db9232560cc"
DEVSUM:x86-64 = "0bc5447c0a6a48aae5457fc82ffd57a7ec0c9665d68617c0593feed4941955a5"

RDEPENDS:${PN} = "libnvjitlink"
RDEPENDS:${PN}-stubs = "libnvjitlink"
BBCLASSEXTEND = "native nativesdk"
