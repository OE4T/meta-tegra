require gcc-8-configure-common.inc

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

RUNTIMETARGET = "${RUNTIMELIBSSP} libstdc++-v3 libgomp libatomic ${RUNTIMELIBITM}"

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
	rm -rf ${D}${libdir}/gcc ${D}${datadir}
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
DEPENDS = "virtual/${TARGET_PREFIX}cuda-gcc virtual/${TARGET_PREFIX}cuda-g++ libgcc-8 virtual/${MLPREFIX}libc virtual/${TARGET_PREFIX}compilerlibs"
STAGING_BINDIR_TOOLCHAIN:append = "-${BINV}"
#PROVIDES = "virtual/${TARGET_PREFIX}cuda-compilerlibs"

BBCLASSEXTEND = "nativesdk"

FILES:${PN}-dev += "${libdir}/${TARGET_SYS}"
RDEPENDS:${PN}-dev = ""
