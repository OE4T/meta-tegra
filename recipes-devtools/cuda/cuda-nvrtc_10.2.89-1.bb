require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "fe9cc7cbdab29035371e5f77d190aecd97bbf31ca66b3f62612bf62580417b16"
SRC_URI[dev.sha256sum] = "5c3bd9bc84170ec0c3465444409e7912046e2523e6a18d331b53901c0e57d229"

BBCLASSEXTEND = "native nativesdk"
