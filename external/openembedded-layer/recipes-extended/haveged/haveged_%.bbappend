do_install_append_tegra() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        sed -i -e "s,@SBIN_DIR@,${sbindir},g" ${D}${INIT_D_DIR}/haveged
    fi
}
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
