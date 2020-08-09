require recipes-kernel/kern-tools/kern-tools-native_git.bb

SRC_URI += "file://0001-Add-kernel-overlays-support-to-kconfiglib.patch"

do_install() {
    oe_runmake DESTDIR=${D}${bindir}/${BPN} install
}
