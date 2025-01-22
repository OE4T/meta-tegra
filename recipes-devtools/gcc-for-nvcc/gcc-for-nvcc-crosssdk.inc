inherit crosssdk

PN = "gcc-for-nvcc-crosssdk-${SDK_SYS}"

SYSTEMHEADERS = "${SDKPATHNATIVE}${prefix_nativesdk}/include"
SYSTEMLIBS = "${SDKPATHNATIVE}${base_libdir_nativesdk}/"
SYSTEMLIBS1 = "${SDKPATHNATIVE}${libdir_nativesdk}/"

GCCMULTILIB = "--disable-multilib"

DEPENDS = "virtual/nativesdk-cross-binutils gettext-native ${NATIVEDEPS}"
PROVIDES = "virtual/nativesdk-cross-cuda-gcc virtual/nativesdk-cross-cuda-g++"
