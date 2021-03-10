require gcc-${PV}.inc
require gcc-runtime.inc

# Disable ifuncs for libatomic on arm conflicts -march/-mcpu
EXTRA_OECONF_append_arm = " libat_cv_have_ifunc=no "

# Building with thumb enabled on armv6t fails
ARM_INSTRUCTION_SET_armv6 = "arm"
