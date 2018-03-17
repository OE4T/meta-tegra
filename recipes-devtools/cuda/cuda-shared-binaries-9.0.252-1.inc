CUDA_PKG ?= "${BPN}"
REPLACE_STUBS ?= "1"

do_fetch() {
    :
}
do_fetch[noexec] = "1"
deltask do_unpack
deltask do_patch

DESCRIPTION = "CUDA package ${PN}"
SRC_URI = ""
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-9.0/LICENSE;md5=d87e5ceb8a41dd716b3205c4e00aa729 \
	            file://usr/local/cuda-9.0/doc/EULA.txt;md5=1d9340fbe1f77282520c3ef05235c26a"
do_configure[depends] += "${MLPREFIX}cuda-binaries:do_preconfigure dpkg-native:do_populate_sysroot"
do_populate_lic[depends] += "${MLPREFIX}cuda-binaries:do_preconfigure"

PR = "r0"

S = "${TMPDIR}/work-shared/cuda-binaries-${PV}-${PR}"
B = "${WORKDIR}/build"

DEPENDS ?= "cuda-cudart"

do_compile() {
    set -x
    if [ -f ${S}/var/cuda-repo-9-0-local/${CUDA_PKG}-9-0_${PV}_arm64.deb ]; then
        dpkg-deb --extract ${S}/var/cuda-repo-9-0-local/${CUDA_PKG}-9-0_${PV}_arm64.deb ${B}
    fi
    if [ -f ${S}/var/cuda-repo-9-0-local/${CUDA_PKG}-dev-9-0_${PV}_arm64.deb ]; then
        dpkg-deb --extract ${S}/var/cuda-repo-9-0-local/${CUDA_PKG}-dev-9-0_${PV}_arm64.deb ${B}
    fi
    rm -f ${B}/usr/local/cuda-9.0/lib64
    [ -L ${B}/usr/local/cuda-9.0/include ] && rm ${B}/usr/local/cuda-9.0/include
    if [ -d ${B}/usr/local/cuda-9.0/targets/aarch64-linux ]; then
        oldwd="$PWD"
	cd ${B}/usr/local/cuda-9.0/targets/aarch64-linux
	for d in *; do
	    [ -d $d ] || continue
	    if [ -d ${B}/usr/local/cuda-9.0/$d ]; then
	        mv $d/* ${B}/usr/local/cuda-9.0/$d/
		rmdir $d
	    else
	        mv $d ${B}/usr/local/cuda-9.0/
	    fi
	done
	cd "$oldwd"
	rmdir ${B}/usr/local/cuda-9.0/targets/aarch64-linux
	rmdir ${B}/usr/local/cuda-9.0/targets
    fi
    if [ "${REPLACE_STUBS}" = "1" ]; then
        for f in ${B}/usr/local/cuda-9.0/lib/stubs/lib*.so; do
	    [ -f $f ] || continue
	    fname=`basename $f`
	    ln -sf ../${fname}.9.0 $f
        done
    fi
    if [ -d ${B}/usr/lib/pkgconfig ]; then
        for f in ${B}/usr/lib/pkgconfig/*; do
            sed -i -r -e's,^(libdir=.*/)lib[^/]*$,\1${baselib},' \
                -e's,^(libdir=.*/)lib[^/]*(/.*)$,\1${baselib}\2,' \
                -e's!^(Libs:.*)!\1 -Wl,-rpath=$!' $f
            sed -i -re's,^(Libs:.*),\1{libdir},' $f
	done
    fi
    if [ "${baselib}" != "lib" -a -d ${B}/usr/lib ]; then
        mv ${B}/usr/lib ${B}/usr/${baselib}
    fi
    if [ "${baselib}" != "lib" -a -d ${B}/usr/local/cuda-9.0/lib ]; then
        mv ${B}/usr/local/cuda-9.0/lib ${B}/usr/local/cuda-9.0/${baselib}
    fi
}
do_compile[cleandirs] = "${B}"

do_install() {
    install -d ${D}${prefix}
    cp -R --preserve=mode,timestamps ${B}/usr/* ${D}${prefix}/
}

FILES_${PN} += "${prefix}/local/cuda-9.0/${baselib}/*${SOLIBS}"
FILES_${PN}-dev += "${prefix}/local/cuda-9.0/include ${prefix}/local/cuda-9.0/${baselib}/stubs ${prefix}/local/cuda-9.0/${baselib}/*${SOLIBSDEV}"
FILES_${PN}-staticdev += "${prefix}/local/cuda-9.0/${baselib}/*.a"
PRIVATE_LIBS = "libcuda.so.1"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

INSANE_SKIP_${PN} = "ldflags libdir"
INSANE_SKIP_${PN}-dev = "ldflags libdir dev-elf"

sysroot_stage_dirs_append() {
    sysroot_stage_dir $from${prefix}/local/cuda-9.0 $to${prefix}/local/cuda-9.0
}

COMPATIBLE_MACHINE = "(tegra210|tegra186)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
