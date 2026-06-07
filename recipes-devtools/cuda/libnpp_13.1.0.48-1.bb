CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "fa791ddeb0c7cdda909469189f67e67d7478aee68e7c73934dd9c1d68e805030"
MAINSUM:x86-64 = "e1e142b9efb93ef9fba2c1138a3ae4d3f2f8d6b332ef128f6206fc6dbfbe23ea"
DEVSUM = "17a4738cffec0757f98bcd6c8d22f24f2e534a69a35e1d00f2c37b9411883bf6"
DEVSUM:x86-64 = "8810f3e3d5be2a8060fd81adfc5b117a6ee6b25a5e06de70852bcd1fff04a1c5"

BBCLASSEXTEND = "native nativesdk"
