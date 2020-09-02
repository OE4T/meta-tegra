CUDA_PKG ?= "${BPN}"
REPLACE_STUBS ?= "1"

require cuda-binaries-common-defs.inc

do_fetch() {
    :
}
do_fetch[noexec] = "1"
deltask do_unpack
deltask do_patch

DESCRIPTION = "CUDA package ${PN}"
SRC_URI = ""
LICENSE = "Proprietary"
HOMEPAGE = "https://developer.nvidia.com/embedded/linux-tegra"
LIC_FILES_CHKSUM = "file://usr/local/cuda-10.0/LICENSE;md5=dfb2d23fe5070ac47b201fbf1e497891 \
	            file://usr/local/cuda-10.0/doc/EULA.txt;md5=049f8f2a31b8c3cc436cc63f0a72c9c9"
CUDA_BINARIES_NATIVE ??= "cuda-binaries-ubuntu1804-native"
CUDABINDEP = "${MLPREFIX}cuda-binaries"
CUDABINDEP_x86-64 = "${CUDA_BINARIES_NATIVE}"
do_configure[depends] += "${CUDABINDEP}:do_preconfigure dpkg-native:do_populate_sysroot"
do_populate_lic[depends] += "${CUDABINDEP}:do_preconfigure"

DEPENDS ?= "cuda-cudart"
CUDA_VERSION_DASHED = "${@d.getVar('CUDA_VERSION').replace('.','-')}"

do_compile() {
    for pkg in ${CUDA_PKG}; do
	extracts=""
	if [ -f ${S}/var/cuda-repo-${CUDA_VERSION_DASHED}-local${CUDA_REPO_EXTRA}/$pkg-${CUDA_VERSION_DASHED}_${PV}_${CUDA_DEB_PKGARCH}.deb ]; then
            dpkg-deb --extract ${S}/var/cuda-repo-${CUDA_VERSION_DASHED}-local${CUDA_REPO_EXTRA}/$pkg-${CUDA_VERSION_DASHED}_${PV}_${CUDA_DEB_PKGARCH}.deb ${B}
	    extracts="${extracts} $pkg"
	fi
	if [ -f ${S}/var/cuda-repo-${CUDA_VERSION_DASHED}-local${CUDA_REPO_EXTRA}/$pkg-dev-${CUDA_VERSION_DASHED}_${PV}_${CUDA_DEB_PKGARCH}.deb ]; then
            dpkg-deb --extract ${S}/var/cuda-repo-${CUDA_VERSION_DASHED}-local${CUDA_REPO_EXTRA}/$pkg-dev-${CUDA_VERSION_DASHED}_${PV}_${CUDA_DEB_PKGARCH}.deb ${B}
	    extracts="${extracts} $pkg-dev"
	fi
	if [ -z "$extracts" ]; then
	    bbwarn "No packages extracted for: $pkg"
	fi
    done
    rm -f ${B}/usr/local/cuda-10.0/lib64
    [ -L ${B}/usr/local/cuda-10.0/include ] && rm ${B}/usr/local/cuda-10.0/include
    if [ -d ${B}/usr/local/cuda-10.0/targets/${HOST_ARCH}-${HOST_OS} ]; then
        oldwd="$PWD"
	cd ${B}/usr/local/cuda-10.0/targets/${HOST_ARCH}-${HOST_OS}
	for d in *; do
	    [ -d $d ] || continue
	    if [ -d ${B}/usr/local/cuda-10.0/$d ]; then
	        mv $d/* ${B}/usr/local/cuda-10.0/$d/
		rmdir $d
	    else
	        mv $d ${B}/usr/local/cuda-10.0/
	    fi
	done
	cd "$oldwd"
	rmdir ${B}/usr/local/cuda-10.0/targets/${HOST_ARCH}-${HOST_OS}
	rmdir ${B}/usr/local/cuda-10.0/targets
    fi
    if [ "${REPLACE_STUBS}" = "1" ]; then
        for f in ${B}/usr/local/cuda-10.0/lib/stubs/lib*.so; do
	    [ -f $f ] || continue
	    fname=`basename $f`
	    ln -sf ../${fname}.10.0 $f
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
    if [ "${baselib}" != "lib" ]; then
        if [ -d ${B}/usr/lib ]; then
            mv ${B}/usr/lib ${B}/usr/${baselib}
	fi
	if [ -d ${B}/usr/local/cuda-10.0/lib ]; then
            mv ${B}/usr/local/cuda-10.0/lib ${B}/usr/local/cuda-10.0/${baselib}
	fi
    fi
}
do_compile[cleandirs] = "${B}"

do_install() {
    install -d ${D}${prefix}
    cp -R --preserve=mode,timestamps ${B}/usr/* ${D}${prefix}/
}

FILES_${PN} += "${prefix}/local/cuda-10.0/${baselib}/*${SOLIBS} ${prefix}/local/cuda-10.0/bin"
FILES_${PN}-dev += "${prefix}/local/cuda-10.0/extras ${prefix}/local/cuda-10.0/include ${prefix}/local/cuda-10.0/${baselib}/stubs \
                    ${prefix}/local/cuda-10.0/${baselib}/*${SOLIBSDEV}"
FILES_${PN}-staticdev += "${prefix}/local/cuda-10.0/${baselib}/*.a"
RDEPENDS_${PN}_class-target = "tegra-libraries"

INSANE_SKIP_${PN} = "ldflags libdir"
INSANE_SKIP_${PN}-dev = "ldflags libdir dev-elf"

sysroot_stage_dirs_append() {
    sysroot_stage_dir $from${prefix}/local/cuda-10.0 $to${prefix}/local/cuda-10.0
}

inherit container-runtime-csv
CONTAINER_CSV_FILES = "${prefix}/local/*"

COMPATIBLE_MACHINE_class-target = "(tegra)"
PACKAGE_ARCH_class-target = "${SOC_FAMILY_PKGARCH}"
