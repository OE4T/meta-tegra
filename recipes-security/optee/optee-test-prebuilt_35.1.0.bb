DESCRIPTION = "Prebuilt Test suite for TEE"

require optee-prebuilt.inc

DEPENDS = "optee-client-prebuilt"

LIC_FILES_CHKSUM += " \
    file://usr/share/doc/nvidia-tegra/LICENSE.optee_test;md5=daa2bcccc666345ab8940aab1315a4fa \
"

do_install() {
    install -d ${D}${base_bindir}
    install -m 0755 ${S}/bin/xtest ${D}${base_bindir}

    install -d ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/023f8f1a-292a-432b-8fc4-de8471358067.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/380231ac-fb99-47ad-a689-9e017eb6e78a.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/528938ce-fc59-11e8-8eb2-f2801f1b9fd1.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/5b9e0e40-2636-11e1-ad9e-0002a5d5c51b.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/5ce0c432-0ab0-40e5-a056-782ca0e6aba2.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/614789f2-39c0-4ebf-b235-92b32ac107ed.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/731e279e-aafb-4575-a771-38caa6f0cca6.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/873bcd08-c2c3-11e6-a937-d0bf9c45c61c.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/a4c04d50-f180-11e8-8eb2-f2801f1b9fd1.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/b3091a65-9751-4784-abf7-0298a7cc35ba.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/b689f2a7-8adf-477a-9f99-32e90c0ad0a2.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/c3f6e2c0-3548-11e1-b86c-0800200c9a66.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/cb3e5ba0-adf1-11e0-998b-0002a5d5c51b.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/d17f73a0-36ef-11e1-984a-0002a5d5c51b.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/e13010e0-2ae1-11e5-896a-0002a5d5c51b.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/e626662e-c0e2-485c-b8c8-09fbce6edf3d.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/e6a33ed4-562b-463a-bb7e-ff5e15a493c8.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/f04a0fe7-1f5d-4b9b-abf7-619b85b4ce8c.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/f157cda0-550c-11e5-a6fa-0002a5d5c51b.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/fd02c9da-306c-48c7-a49c-bbd827ae86ee.ta ${D}${base_libdir}/optee_armtz
    install -m 0644 ${S}/lib/optee_armtz/ffd2bded-ab7d-4988-95ee-e4962fff7154.ta ${D}${base_libdir}/optee_armtz
}

FILES:${PN} += "${base_libdir}/optee_armtz"
