require cuda-shared-binaries.inc

MAINSUM = "c665700c6aae6a4aace16a7b1ff833fd73850a6fdafb41ed04f2e328a2769b9b"
MAINSUM:x86-64 = "dd9377a32bc4a236a3626ac91156e7e1b661ea48bce5aafc5dbd0a91c5e892a0"
DEVSUM = "e7f4a85c4fcd99c236be4c336db1a6f985168a98bd8f1e94609e8db9232560cc"
DEVSUM:x86-64 = "2b1003328eb1d55484bbe785f0e334be5933184d3489a7e69d4c7ec8854ccd89"

RDEPENDS:${PN} = "libnvjitlink"
RDEPENDS:${PN}-stubs = "libnvjitlink"
BBCLASSEXTEND = "native nativesdk"
