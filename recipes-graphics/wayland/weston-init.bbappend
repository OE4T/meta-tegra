FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:tegra = "\
    file://weston-tegra-overrides.conf \
    file://xwayland-tegra-start.sh \
"

XWAYLAND_SUPPORT = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', '', '#', d)}"

do_install:append:tegra() {
    install -d ${D}${sysconfdir}/systemd/system/weston.service.d
    install -m 0644 ${WORKDIR}/weston-tegra-overrides.conf ${D}${sysconfdir}/systemd/system/weston.service.d/
    install -d ${D}${bindir}
    if [ -z "${XWAYLAND_SUPPORT}" ]; then
        install -m 0755 ${WORKDIR}/xwayland-tegra-start.sh ${D}${bindir}/xwayland-tegra-start
    fi
    sed -i -e's,@XWAYLAND@,${XWAYLAND_SUPPORT},' ${D}${sysconfdir}/xdg/weston/weston.ini
}

PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
