require gcc-${PV}.inc
require gcc-runtime.inc

FILES:libgomp-dev += "\
    ${libdir}/gcc/${TARGET_SYS}/${BINV}/include/openacc.h \
"

