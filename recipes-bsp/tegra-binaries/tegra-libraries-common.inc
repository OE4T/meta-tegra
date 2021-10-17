require tegra-binaries-32.6.1.inc
require tegra-shared-binaries.inc

inherit container-runtime-csv

CONTAINER_CSV_FILES = "${libdir}/*.so*"
TEGRA_LIBRARIES_EXTRACT ?= "usr/lib"

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 ${TEGRA_LIBRARIES_EXTRACT}
}

do_compile[noexec] = "1"

DRVROOT = "${B}/usr/lib/aarch64-linux-gnu"
TEGRA_LIBRARIES_TO_INSTALL ??= ""

install_libraries() {
    install -d ${D}${libdir}
    for f in ${TEGRA_LIBRARIES_TO_INSTALL}; do
        fbasename=$(basename "$f")
	install -m 0644 ${DRVROOT}/$f ${D}${libdir}/$fbasename
    done
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP = "dev-so ldflags"
