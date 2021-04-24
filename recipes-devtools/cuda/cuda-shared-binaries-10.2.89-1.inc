CUDA_PKG ?= "${BPN} ${BPN}-dev"
REPLACE_STUBS ?= "1"

require cuda-binaries-common-defs.inc

L4T_DEB_GROUP ?= "cuda"
L4T_DEB_FEED_BASE ??= "file://"
SRC_COMMON_DEBS = "${@' '.join(['%s-${CUDA_VERSION_DASHED}_${PV}_${CUDA_DEB_PKGARCH}.deb;name=%s;subdir=${BP}' \
                                % (pkg, 'dev' if pkg.endswith('-dev') else 'main') for pkg in d.getVar('CUDA_PKG').split()])} \
		   cuda-license-${CUDA_VERSION_DASHED}_${PV}_${CUDA_DEB_PKGARCH}.deb;name=lic;subdir=${BP}"

SRC_URI[main.sha256sum] = "${MAINSUM}"
SRC_URI[dev.sha256sum] = "${DEVSUM}"
LICSUM = "8a862acbff5b33904bfe7ec3e92a553a8312da1db9f651b6cfe14db137a139ce"
LICSUM_x86-64 = "daf3f9891b290b64ae1130546aaadabd6631b44d08c69a76a74e94a111b6ecec"
SRC_URI[lic.sha256sum] = "${LICSUM}"

CUDA_DL_CLASS = "l4t_deb_pkgfeed"

inherit ${CUDA_DL_CLASS}

DESCRIPTION = "CUDA package ${PN}"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-10.2/LICENSE;md5=659d2cacdb888a4ed2aa29014ed0302a \
	            file://usr/local/cuda-10.2/doc/EULA.txt;md5=37774d0b88c5743e8fe8e5c10b057270"

DEPENDS ?= "cuda-cudart"
CUDA_VERSION_DASHED = "${@d.getVar('CUDA_VERSION').replace('.','-')}"

CUDA_PKGNAMES ?= "${@' '.join(['%s-${CUDA_VERSION_DASHED}_${PV}_${CUDA_DEB_PKGARCH}.deb' % pkg for pkg in d.getVar('CUDA_PKG').split()])}"

do_compile() {
    rm -f ${B}/usr/local/cuda-${CUDA_VERSION}/lib64
    [ -L ${B}/usr/local/cuda-${CUDA_VERSION}/include ] && rm ${B}/usr/local/cuda-${CUDA_VERSION}/include
    if [ -d ${B}/usr/local/cuda-${CUDA_VERSION}/targets/${HOST_ARCH}-${HOST_OS} ]; then
        oldwd="$PWD"
	cd ${B}/usr/local/cuda-${CUDA_VERSION}/targets/${HOST_ARCH}-${HOST_OS}
	for d in *; do
	    [ -d $d ] || continue
	    if [ -d ${B}/usr/local/cuda-${CUDA_VERSION}/$d ]; then
	        mv $d/* ${B}/usr/local/cuda-${CUDA_VERSION}/$d/
		rmdir $d
	    else
	        mv $d ${B}/usr/local/cuda-${CUDA_VERSION}/
	    fi
	done
	cd "$oldwd"
	rmdir ${B}/usr/local/cuda-${CUDA_VERSION}/targets/${HOST_ARCH}-${HOST_OS}
	rmdir ${B}/usr/local/cuda-${CUDA_VERSION}/targets
    fi
    if [ "${REPLACE_STUBS}" = "1" ]; then
        for f in ${B}/usr/local/cuda-${CUDA_VERSION}/lib/stubs/lib*.so; do
	    [ -f $f ] || continue
	    fname=`basename $f`
	    ln -sf ../${fname}.${CUDA_VERSION} $f
        done
    fi
    if [ -d ${B}/usr/lib/pkgconfig ]; then
        for f in ${B}/usr/lib/pkgconfig/*; do
            sed -i -r -e's,^(libdir=.*/)lib[^/]*$,\1${baselib},' \
		-e's,/targets/${HOST_ARCH}-${HOST_OS},,g' \
                -e's,^(libdir=.*/)lib[^/]*(/.*)$,\1${baselib}\2,' \
                -e's!^(Libs:.*)!\1 -Wl,-rpath=$!' $f
            sed -i -re's,^(Libs:.*),\1{libdir},' $f
	done
    fi
    if [ "${baselib}" != "lib" ]; then
        if [ -d ${B}/usr/lib ]; then
            mv ${B}/usr/lib ${B}/usr/${baselib}
	fi
	if [ -d ${B}/usr/local/cuda-${CUDA_VERSION}/lib ]; then
            mv ${B}/usr/local/cuda-${CUDA_VERSION}/lib ${B}/usr/local/cuda-${CUDA_VERSION}/${baselib}
	fi
    fi
    rm -rf ${B}/usr/share/doc
    if [ -d "${B}/usr/share" ]; then
        [ -n "$(ls ${B}/usr/share)" ] || rmdir ${B}/usr/share
    fi
}

do_install() {
    install -d ${D}${prefix}
    cp -R --preserve=mode,timestamps ${B}/usr/* ${D}${prefix}/
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/doc
    rm -f ${D}${prefix}/local/cuda-${CUDA_VERSION}/LICENSE ${D}${prefix}/local/cuda-${CUDA_VERSION}/README ${D}${prefix}/local/cuda-${CUDA_VERSION}/version.txt
}

FILES_${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*${SOLIBS} ${prefix}/local/cuda-${CUDA_VERSION}/bin"
FILES_${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/extras ${prefix}/local/cuda-${CUDA_VERSION}/include ${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/stubs \
                    ${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*${SOLIBSDEV}"
FILES_${PN}-staticdev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*.a"
RDEPENDS_${PN}_class-target = "tegra-libraries"

INSANE_SKIP_${PN} = "ldflags libdir"
INSANE_SKIP_${PN}-dev = "ldflags libdir dev-elf"

sysroot_stage_dirs_append() {
    sysroot_stage_dir $from${prefix}/local/cuda-${CUDA_VERSION} $to${prefix}/local/cuda-${CUDA_VERSION}
}

inherit container-runtime-csv
CONTAINER_CSV_FILES = "${prefix}/local/*"

COMPATIBLE_MACHINE_class-target = "(tegra)"
PACKAGE_ARCH_class-target = "${SOC_FAMILY_PKGARCH}"
