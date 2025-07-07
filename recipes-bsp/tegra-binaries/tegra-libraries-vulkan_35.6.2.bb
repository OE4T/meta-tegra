L4T_DEB_COPYRIGHT_MD5 = "8c7016b98a9864afb8cc0a7eb8ba62fa"
DEPENDS = "tegra-libraries-core tegra-libraries-eglcore virtual/egl egl-wayland"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "064c594b3a297c40d85abe14c11254f8365c4c61a5a0a022f15101e21a918787"

inherit features_check

REQUIRED_DISTRO_FEATURES = "vulkan opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvidia-vulkan-producer.so \
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
