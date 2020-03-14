do_install_append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)};    then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/docker.init ${D}${sysconfdir}/init.d/docker.init
    fi
}

RRECOMMENDS_${PN}_append = " \
    kernel-module-br-netfilter \
    kernel-module-esp4 \
    kernel-module-ip-vs \
    kernel-module-ip-vs-rr \
    kernel-module-macvlan \
    kernel-module-nf-conntrack-netlink \
    kernel-module-nf-nat-ftp \
    kernel-module-nf-nat-redirect \
    kernel-module-nf-nat-tftp \
    kernel-module-overlay \
    kernel-module-veth \
    kernel-module-xt-addrtype \
    kernel-module-xt-conntrack \
    kernel-module-xt-redirect \
"
