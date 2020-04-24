
require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "f800974ac6cb3fb6595a08e613fc0f376d7b91f43eed1b5306b0496f9588e441"
SRC_URI[dev.sha256sum] = "cc2d9897c54f27a20f90bea5df391875b3f8163ec69745fb7546c1ce57e1d718"

BBCLASSEXTEND = "native nativesdk"
