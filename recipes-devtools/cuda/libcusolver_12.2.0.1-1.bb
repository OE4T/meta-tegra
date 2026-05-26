CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "4f55310a03152271b23d7edc02c57135db80e49dd5832fc346abf80987a71722"
MAINSUM:x86-64 = "559390bd8aecd10738e4b46a5883dce9eb73f3a42e4e8ccd3b59c72984d54350"
DEVSUM = "e4f26719c484d4611ba479942e6e92a80f46cd5e822561120ef843600c82aff7"
DEVSUM:x86-64 = "6b9d4ac6c093809e55c34450238e25e78f4fe1c7edf66a17e3fed5def986e95a"

RDEPENDS:${PN} = "libcublas libcusparse libnvjitlink"
BBCLASSEXTEND = "native nativesdk"
