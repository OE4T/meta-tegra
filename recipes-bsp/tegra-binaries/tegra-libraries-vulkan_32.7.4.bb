L4T_DEB_COPYRIGHT_MD5 = "ce4d36df31e6cc73581fd2a25d16834e"
DEPENDS = "tegra-libraries-core tegra-libraries-eglcore virtual/egl egl-wayland"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "d65d21276997377e5fb744e25439d19c264cf1172c5a223a94a45e0d9964af79"
MAINSUM:tegra210 = "6ff6d996a737f62230cc3b55f63a737f30ca1daf68dc52cecd1247d92341d95d"

inherit features_check

REQUIRED_DISTRO_FEATURES = "vulkan opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvvulkan-producer.so \
"

do_install() {
    install_libraries
    install -d ${D}/usr/lib/aarch64-linux-gnu/tegra
    if ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'true', 'false', d)}; then
        install -m644 ${S}/usr/lib/aarch64-linux-gnu/tegra/nvidia_icd.json ${D}/usr/lib/aarch64-linux-gnu/tegra/
        install -d ${D}${sysconfdir}/vulkan/icd.d
        ln -sf /usr/lib/aarch64-linux-gnu/tegra/nvidia_icd.json ${D}${sysconfdir}/vulkan/icd.d/
    fi
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
FILES:${PN} += "/usr/lib/aarch64-linux-gnu"
CONTAINER_CSV_FILES:append = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', ' ${sysconfdir}/vulkan/icd.d/* /usr/lib/aarch64-linux-gnu/tegra/nvidia_icd.json', '', d)}"
