require gcc-${PV}.inc
require libgcc-8-initial.inc

# Building with thumb enabled on armv6t fails
ARM_INSTRUCTION_SET:armv6 = "arm"
