SUMMARY = "Host packages for CUDA SDK toolchain"

inherit packagegroup
inherit_defer nativesdk

PACKAGEGROUP_DISABLE_COMPLEMENTARY = "1"

RDEPENDS:${PN} = "\
    nativesdk-cuda-compiler \
"
