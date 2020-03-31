SUMMARY = "Host packages for CUDA SDK toolchain"

inherit packagegroup nativesdk

PACKAGEGROUP_DISABLE_COMPLEMENTARY = "1"

RDEPENDS_${PN} = "\
    nativesdk-cuda-compiler \
"
