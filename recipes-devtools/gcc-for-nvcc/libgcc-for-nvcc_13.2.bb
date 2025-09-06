require gcc-for-nvcc-${PV}.inc
require libgcc-for-nvcc.inc

LDFLAGS += "-fuse-ld=bfd"

# Building with thumb enabled on armv6t fails
ARM_INSTRUCTION_SET:armv6 = "arm"
