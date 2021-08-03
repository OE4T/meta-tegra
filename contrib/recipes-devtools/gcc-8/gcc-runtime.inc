require gcc-configure-common.inc

SUMMARY = "Runtime libraries from GCC"

# Over-ride the LICENSE set by gcc-${PV}.inc to remove "& GPLv3"
# All gcc-runtime packages are now covered by the runtime exception.
LICENSE = "GPL-3.0-with-GCC-exception"

CXXFLAGS:remove = "-fvisibility-inlines-hidden"

EXTRA_OECONF_PATHS = "\
    --with-gxx-include-dir=${includedir}/c++/${BINV} \
    --with-sysroot=/not/exist \
    --with-build-sysroot=${STAGING_DIR_TARGET} \
"

EXTRA_OECONF:append:linuxstdbase = " --enable-clocale=gnu"
EXTRA_OECONF:append = " --cache-file=${B}/config.cache"

RUNTIMELIBITM = "libitm"
RUNTIMELIBITM:arc = ""
RUNTIMELIBITM:mipsarch = ""
RUNTIMELIBITM:nios2 = ""
RUNTIMELIBITM:microblaze = ""
RUNTIMELIBITM:riscv32 = ""
RUNTIMELIBITM:riscv64 = ""
RUNTIMELIBSSP ?= ""
RUNTIMELIBSSP:mingw32 ?= "libssp"

RUNTIMETARGET = "${RUNTIMELIBSSP} libstdc++-v3 libgomp libatomic ${RUNTIMELIBITM} \
    ${@bb.utils.contains_any('FORTRAN', [',fortran',',f77'], 'libquadmath', '', d)} \
"

# libiberty
# libgfortran needs separate recipe due to libquadmath dependency

SLIB = "${TMPDIR}/work-shared/gcc-${PV}-${PR}/gcc-${PV}"
SLIB_NEW = "/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}"

DEBUG_PREFIX_MAP:class-target = " \
   -fdebug-prefix-map=${WORKDIR}/recipe-sysroot= \
   -fdebug-prefix-map=${WORKDIR}/recipe-sysroot-native= \
   -fdebug-prefix-map=${SLIB}=${SLIB_NEW} \
   -fdebug-prefix-map=${SLIB}/include=${SLIB_NEW}/libstdc++-v3/../include \
   -fdebug-prefix-map=${SLIB}/libiberty=${SLIB_NEW}/libstdc++-v3/../libiberty \
   -fdebug-prefix-map=${B}=${SLIB_NEW} \
   "

do_configure () {
	export CXX="${CXX} -nostdinc++ -nostdlib++"
	for d in libgcc ${RUNTIMETARGET}; do
		echo "Configuring $d"
		rm -rf ${B}/${TARGET_SYS}/$d/
		mkdir -p ${B}/${TARGET_SYS}/$d/
		cd ${B}/${TARGET_SYS}/$d/
		chmod a+x ${S}/$d/configure
		relpath=${@os.path.relpath("${S}/$d", "${B}/${TARGET_SYS}/$d")}
		$relpath/configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
		if [ "$d" = "libgcc" ]; then
			(cd ${B}/${TARGET_SYS}/libgcc; oe_runmake enable-execute-stack.c unwind.h md-unwind-support.h sfp-machine.h gthr-default.h)
		fi
	done
}
EXTRACONFFUNCS += "extract_stashed_builddir"
do_configure[depends] += "${COMPILERDEP}"

do_compile () {
	for d in libgcc ${RUNTIMETARGET}; do
		cd ${B}/${TARGET_SYS}/$d/
		oe_runmake MULTIBUILDTOP=${B}/${TARGET_SYS}/$d/
	done
}

do_install () {
	for d in ${RUNTIMETARGET}; do
		cd ${B}/${TARGET_SYS}/$d/
		oe_runmake 'DESTDIR=${D}' MULTIBUILDTOP=${B}/${TARGET_SYS}/$d/ install
	done
	if [ -d ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/include ]; then
		install -d ${D}${libdir}/${TARGET_SYS}/${BINV}/include 
		mv ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/include/* ${D}${libdir}/${TARGET_SYS}/${BINV}/include
		rmdir --ignore-fail-on-non-empty -p ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/include
	fi
	rm -rf ${D}${infodir}/libgomp.info ${D}${infodir}/dir
	rm -rf ${D}${infodir}/libitm.info ${D}${infodir}/dir
	rm -rf ${D}${infodir}/libquadmath.info ${D}${infodir}/dir
	if [ -d ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/finclude ]; then
		rmdir --ignore-fail-on-non-empty -p ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/finclude
	fi
	if [ -d ${D}${infodir} ]; then
		rmdir --ignore-fail-on-non-empty -p ${D}${infodir}
	fi
}

do_install:append:class-target () {
	if [ "${TARGET_OS}" = "linux-gnuspe" ]; then
		ln -s ${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-linux
	fi

	if [ "${TARGET_OS}" = "linux-gnun32" ]; then
		if [ "${TARGET_VENDOR_MULTILIB_ORIGINAL}" != "" -a "${TARGET_VENDOR}" != "${TARGET_VENDOR_MULTILIB_ORIGINAL}" ]; then
			mkdir ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR_MULTILIB_ORIGINAL}-linux
			ln -s ../${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR_MULTILIB_ORIGINAL}-linux/32
		elif [ "${MULTILIB_VARIANTS}" != "" ]; then
			mkdir ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-linux
			ln -s ../${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-linux/32
		else
			ln -s ${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-linux
		fi
	elif [ "${TARGET_OS}" = "linux-gnux32" ]; then
		if [ "${TARGET_VENDOR_MULTILIB_ORIGINAL}" != "" -a "${TARGET_VENDOR}" != "${TARGET_VENDOR_MULTILIB_ORIGINAL}" ]; then
			mkdir ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR_MULTILIB_ORIGINAL}-linux
			ln -s ../${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR_MULTILIB_ORIGINAL}-linux/x32
		elif [ "${MULTILIB_VARIANTS}" != "" ]; then
			mkdir ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-linux
			ln -s ../${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-linux/32
		else
			ln -s ${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-linux
		fi
	elif [ "${TARGET_VENDOR_MULTILIB_ORIGINAL}" != "" -a "${TARGET_VENDOR}" != "${TARGET_VENDOR_MULTILIB_ORIGINAL}" ]; then
		mkdir ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR_MULTILIB_ORIGINAL}-${TARGET_OS}
		ln -s ../${TARGET_SYS}/bits ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR_MULTILIB_ORIGINAL}-${TARGET_OS}/bits
		ln -s ../${TARGET_SYS}/ext ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR_MULTILIB_ORIGINAL}-${TARGET_OS}/ext
	fi

	if [ "${TARGET_ARCH}" == "x86_64" -a "${MULTILIB_VARIANTS}" != "" ];then
		ln -sf ../${X86ARCH32}${TARGET_VENDOR}-${TARGET_OS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-${TARGET_OS}/32
	fi

	if [ "${TCLIBC}" != "glibc" ]; then
		case "${TARGET_OS}" in
			"linux-musl" | "linux-*spe") extra_target_os="linux";;
			"linux-musleabi") extra_target_os="linux-gnueabi";;
			*) extra_target_os="linux";;
		esac
		ln -s ${TARGET_SYS} ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-$extra_target_os
	fi
	chown -R root:root ${D}
}

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "virtual/${TARGET_PREFIX}gcc virtual/${TARGET_PREFIX}g++ libgcc virtual/${MLPREFIX}libc"
PROVIDES = "virtual/${TARGET_PREFIX}compilerlibs"

BBCLASSEXTEND = "nativesdk"

PACKAGES = "\
    ${PN}-dbg \
    libstdc++ \
    libstdc++-precompile-dev \
    libstdc++-dev \
    libstdc++-staticdev \
    libg2c \
    libg2c-dev \
    libssp \
    libssp-dev \
    libssp-staticdev \
    libquadmath \
    libquadmath-dev \
    libquadmath-staticdev \
    libgomp \
    libgomp-dev \
    libgomp-staticdev \
    libatomic \
    libatomic-dev \
    libatomic-staticdev \
    libitm \
    libitm-dev \
    libitm-staticdev \
"
# The base package doesn't exist, so we clear the recommends.
RRECOMMENDS:${PN}-dbg = ""

# include python debugging scripts
FILES:${PN}-dbg += "\
    ${libdir}/libstdc++.*-gdb.py \
    ${datadir}/gcc-${BINV}/python/libstdcxx \
"

FILES:libg2c = "${target_libdir}/libg2c.so.*"
SUMMARY:libg2c = "Companion runtime library for g77"
FILES:libg2c-dev = "\
    ${libdir}/libg2c.so \
    ${libdir}/libg2c.a \
    ${libdir}/libfrtbegin.a \
"
SUMMARY:libg2c-dev = "Companion runtime library for g77 - development files"

FILES:libstdc++ = "${libdir}/libstdc++.so.*"
SUMMARY:libstdc++ = "GNU standard C++ library"
FILES:libstdc++-dev = "\
    ${includedir}/c++/ \
    ${libdir}/libstdc++.so \
    ${libdir}/libstdc++*.la \
    ${libdir}/libsupc++.la \
"
SUMMARY:libstdc++-dev = "GNU standard C++ library - development files"
FILES:libstdc++-staticdev = "\
    ${libdir}/libstdc++*.a \
    ${libdir}/libsupc++.a \
"
SUMMARY:libstdc++-staticdev = "GNU standard C++ library - static development files"

FILES:libstdc++-precompile-dev = "${includedir}/c++/${TARGET_SYS}/bits/*.gch"
SUMMARY:libstdc++-precompile-dev = "GNU standard C++ library - precompiled header files"

FILES:libssp = "${libdir}/libssp.so.*"
SUMMARY:libssp = "GNU stack smashing protection library"
FILES:libssp-dev = "\
    ${libdir}/libssp*.so \
    ${libdir}/libssp*_nonshared.a \
    ${libdir}/libssp*.la \
    ${libdir}/${TARGET_SYS}/${BINV}/include/ssp \
"
SUMMARY:libssp-dev = "GNU stack smashing protection library - development files"
FILES:libssp-staticdev = "${libdir}/libssp*.a"
SUMMARY:libssp-staticdev = "GNU stack smashing protection library - static development files"

FILES:libquadmath = "${libdir}/libquadmath*.so.*"
SUMMARY:libquadmath = "GNU quad-precision math library"
FILES:libquadmath-dev = "\
    ${libdir}/${TARGET_SYS}/${BINV}/include/quadmath* \
    ${libdir}/libquadmath*.so \
    ${libdir}/libquadmath.la \
"
SUMMARY:libquadmath-dev = "GNU quad-precision math library - development files"
FILES:libquadmath-staticdev = "${libdir}/libquadmath.a"
SUMMARY:libquadmath-staticdev = "GNU quad-precision math library - static development files"

FILES:libgomp = "${libdir}/libgomp*${SOLIBS}"
SUMMARY:libgomp = "GNU OpenMP parallel programming library"
FILES:libgomp-dev = "\
    ${libdir}/libgomp*${SOLIBSDEV} \
    ${libdir}/libgomp*.la \
    ${libdir}/libgomp.spec \
    ${libdir}/${TARGET_SYS}/${BINV}/include/acc_prof.h \
    ${libdir}/${TARGET_SYS}/${BINV}/include/omp.h \
    ${libdir}/${TARGET_SYS}/${BINV}/include/openacc.h \
"
SUMMARY:libgomp-dev = "GNU OpenMP parallel programming library - development files"
FILES:libgomp-staticdev = "${libdir}/libgomp*.a"
SUMMARY:libgomp-staticdev = "GNU OpenMP parallel programming library - static development files"

FILES:libatomic = "${libdir}/libatomic.so.*"
SUMMARY:libatomic = "GNU C++11 atomics support library"
FILES:libatomic-dev = "\
    ${libdir}/libatomic.so \
    ${libdir}/libatomic.la \
"
SUMMARY:libatomic-dev = "GNU C++11 atomics support library - development files"
FILES:libatomic-staticdev = "${libdir}/libatomic.a"
SUMMARY:libatomic-staticdev = "GNU C++11 atomics support library - static development files"

FILES:libitm = "${libdir}/libitm.so.*"
SUMMARY:libitm = "GNU transactional memory support library"
FILES:libitm-dev = "\
    ${libdir}/libitm.so \
    ${libdir}/libitm.la \
    ${libdir}/libitm.spec \
"
SUMMARY:libitm-dev = "GNU transactional memory support library - development files"
FILES:libitm-staticdev = "${libdir}/libitm.a"
SUMMARY:libitm-staticdev = "GNU transactional memory support library - static development files"
