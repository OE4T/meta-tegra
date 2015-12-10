require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_HOST = "(arm.*)"

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 usr/lib
}

do_compile[noexec] = "1"

DRVROOT = "${B}/usr/lib/arm-linux-gnueabihf"

do_install() {
    install -d ${D}${libdir}
    for f in ${DRVROOT}/tegra/lib*; do    
        install -m 0644 $f ${D}${libdir}
    done
    for f in ${DRVROOT}/tegra-egl/lib*; do    
        install -m 0644 $f ${D}${libdir}
    done
    ln -sf libcuda.so.1.1 ${D}${libdir}/libcuda.so
    ln -sf libGL.so.1 ${D}${libdir}/libGL.so
    ln -sf libEGL.so.1 ${D}${libdir}/libEGL.so
    ln -sf libGLESv1_CM.so.1 ${D}${libdir}/libGLESv1_CM.so
    ln -sf libGLESv2.so.2 ${D}${libdir}/libGLESv2.so
}

python __anonymous() {
    for p in (("libegl", "libegl1"), ("libgl", "libgl1"),
              ("libgles1", "libglesv1-cm1"), ("libgles2", "libglesv2-2")):
        fullp = p[0] + "-tegra"
        pkgs = " ".join(p)
        d.setVar("DEBIAN_NOAUTONAME_" + fullp, "1")
        d.appendVar("RREPLACES_" + fullp, pkgs)
        d.appendVar("RPROVIDES_" + fullp, pkgs)
        d.appendVar("RCONFLICTS_" + fullp, pkgs)

        # For -dev, the first element is both the Debian and original name
        fullp += "-dev"
        pkgs = p[0] + "-dev"
        d.setVar("DEBIAN_NOAUTONAME_" + fullp, "1")
        d.appendVar("RREPLACES_" + fullp, pkgs)
        d.appendVar("RPROVIDES_" + fullp, pkgs)
        d.appendVar("RCONFLICTS_" + fullp, pkgs)
}

PACKAGES = "libegl-tegra libgl-tegra libgles1-tegra libgles2-tegra \
            libegl-tegra-dev libgl-tegra-dev libgles1-tegra-dev libgles2-tegra-dev ${PN}"
FILES_libegl-tegra = "${libdir}/libEGL${SOLIBS}"
FILES_libegl-tegra-dev = "${libdir}/libEGL${SOLIBSDEV}"
FILES_libgl-tegra = "${libdir}/libGL${SOLIBS}"
FILES_libgl-tegra-dev = "${libdir}/libGL${SOLIBSDEV}"
FILES_libgles1-tegra = "${libdir}/libGLESv1*${SOLIBS}"
FILES_libgles1-tegra-dev = "${libdir}/libGLESv1*${SOLIBSDEV}"
FILES_libgles2-tegra = "${libdir}/libGLESv2*${SOLIBS}"
FILES_libgles2-tegra-dev = "${libdir}/libGLESv2*${SOLIBSDEV}"

INSANE_SKIP_${PN} = "dev-so"
