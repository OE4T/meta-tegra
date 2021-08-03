CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

MAINSUM = "e92834f576241295c74393b478cb7121d9e20cb3454aed26c464c3523eaeadde"
MAINSUM:x86-64 = "cdacec79c3ac1ab591a133f11258067b977a0f670de50f2f2355cafe0a34e850"

ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
