CUDA_PKG ?= "${BPN} ${BPN}-dev"

require cuda-binaries-common-defs.inc

L4T_DEB_GROUP ?= "${BPN}"
L4T_DEB_FEED_BASE ??= "file://"
CUDA_LICENSE_PKG ?= "cuda-documentation-${CUDA_VERSION_DASHED}_12.6.68-1_${CUDA_DEB_PKGARCH}.deb;name=lic;subdir=${BP}"
SRC_COMMON_DEBS = "${@' '.join(['%s-${CUDA_VERSION_DASHED}_${PV}_${CUDA_DEB_PKGARCH}.deb;name=%s;subdir=${BP}' \
                                % (pkg, 'dev' if pkg.endswith('-dev') else 'main') for pkg in d.getVar('CUDA_PKG').split()])} \
	           ${CUDA_LICENSE_PKG}"


SRC_URI[main.sha256sum] = "${MAINSUM}"
SRC_URI[dev.sha256sum] = "${DEVSUM}"
LICSUM ?= "fa976b3f74f2fbfe0653ad1796d70fc0910950cca6c4b1eae758ddc26c33551e"
LICSUM:x86-64 ?= "5a7e9103c6cfd60a7ff3290db5f07cf6a3dcbf29799ff0b59c3404a00751928b"
SRC_URI[lic.sha256sum] = "${LICSUM}"
L4T_DEB_GROUP[lic] = "cuda-documentation"

CUDA_DL_CLASS = "l4t_deb_pkgfeed"

inherit ${CUDA_DL_CLASS}

DESCRIPTION = "CUDA package ${PN}"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM ?= "file://usr/local/cuda-${CUDA_VERSION}/EULA.txt;md5=929c95a6164f11b95d0001c9bc136926"

DEPENDS ?= "cuda-cudart"
CUDA_VERSION_DASHED = "${@d.getVar('CUDA_VERSION').replace('.','-')}"

CUDA_PKGNAMES ?= "${@' '.join(['%s-${CUDA_VERSION_DASHED}_${PV}_${CUDA_DEB_PKGARCH}.deb' % pkg for pkg in d.getVar('CUDA_PKG').split()])}"

CUDA_INSTALL_ARCH ?= "${HOST_ARCH}"

do_compile() {
    rm -f ${B}/usr/local/cuda-${CUDA_VERSION}/lib64
    [ -L ${B}/usr/local/cuda-${CUDA_VERSION}/include ] && rm ${B}/usr/local/cuda-${CUDA_VERSION}/include
    if [ -d ${B}/usr/local/cuda-${CUDA_VERSION}/targets/${CUDA_INSTALL_ARCH}-${HOST_OS} ]; then
        oldwd="$PWD"
	cd ${B}/usr/local/cuda-${CUDA_VERSION}/targets/${CUDA_INSTALL_ARCH}-${HOST_OS}
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
	rmdir ${B}/usr/local/cuda-${CUDA_VERSION}/targets/${CUDA_INSTALL_ARCH}-${HOST_OS}
	rmdir ${B}/usr/local/cuda-${CUDA_VERSION}/targets
    fi
    if [ -d ${B}/usr/lib/pkgconfig ]; then
        for f in ${B}/usr/lib/pkgconfig/*; do
            sed -i -r -e's,^(libdir=.*/)lib[^/]*$,\1${baselib},' \
		-e's,/targets/${CUDA_INSTALL_ARCH}-${HOST_OS},,g' \
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
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/doc ${D}${prefix}/local/cuda-${CUDA_VERSION}/tools
    rm -f ${D}${prefix}/local/cuda-${CUDA_VERSION}/LICENSE ${D}${prefix}/local/cuda-${CUDA_VERSION}/README ${D}${prefix}/local/cuda-${CUDA_VERSION}/version.txt
    rm -f ${D}${prefix}/local/cuda-${CUDA_VERSION}/EULA.txt
    rm -f ${D}${prefix}/local/cuda-${CUDA_VERSION}/DOCS
}

PACKAGES =+ "${PN}-stubs"
FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*${SOLIBS} ${prefix}/local/cuda-${CUDA_VERSION}/bin"
FILES:${PN}-stubs = "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/stubs"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/extras ${prefix}/local/cuda-${CUDA_VERSION}/include \
                    ${prefix}/local/cuda-${CUDA_VERSION}/src \
                    ${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*${SOLIBSDEV}"
FILES:${PN}-staticdev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*.a"
RDEPENDS:${PN}-dev = "${PN}-stubs"
ALLOW_EMPTY:${PN}-stubs = "1"
EXCLUDE_PACKAGES_FROM_SHLIBS = "${PN}-stubs"
INSANE_SKIP:${PN} = "ldflags libdir"
INSANE_SKIP:${PN}-dev = "ldflags libdir dev-elf"
INSANE_SKIP:${PN}-stubs = "ldflags libdir dev-so"

sysroot_stage_dirs:append() {
    sysroot_stage_dir $from${prefix}/local/cuda-${CUDA_VERSION} $to${prefix}/local/cuda-${CUDA_VERSION}
}

COMPATIBLE_MACHINE:class-target = "(tegra)"
PACKAGE_ARCH:class-target = "${TEGRA_PKGARCH}"
