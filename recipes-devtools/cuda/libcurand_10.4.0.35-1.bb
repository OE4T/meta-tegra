CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "e4f37d08dda851cefa5693adfdecec3c1a7879e47342755ce8d8c2a1fcea066b"
MAINSUM:x86-64 = "947ed52da5eea5fbc35c273cabaa675a477947d3d9503c77c1982e1c5e680c2b"
DEVSUM = "5a1d55c3ffc7e516bb801dcd2614419146e2551adcbed670f2bda379dd7747f9"
DEVSUM:x86-64 = "69f2a942d106a95cc6bbbb241f6e8b0ee2b477f3e9b1e69fc708ecd858b1847f"

BBCLASSEXTEND = "native nativesdk"
