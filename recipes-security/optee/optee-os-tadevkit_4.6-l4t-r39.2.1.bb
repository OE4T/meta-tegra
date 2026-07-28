SUMMARY = "OP-TEE Trusted OS TA devkit"
DESCRIPTION = "OP-TEE TA devkit for build TAs"
HOMEPAGE = "https://www.op-tee.org/"

FILESEXTRAPATHS:prepend := "${THISDIR}/optee-os:"
require optee-os-l4t.inc

do_install() {
    install -d ${D}${includedir}/optee/export-user_ta/
    for f in ${B}/export-ta_${OPTEE_ARCH}/* ; do
        cp -aR $f ${D}${includedir}/optee/export-user_ta/
    done
    sed -i -r -e's!-f(macro|debug|file)-prefix-map=[^ ]+!!g' ${D}${includedir}/optee/export-user_ta/mk/conf.mk
}

FILES:${PN} = "${includedir}/optee/"
INSANE_SKIP:${PN}-dev = "staticdev"
