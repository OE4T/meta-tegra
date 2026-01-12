L4T_DEB_COPYRIGHT_MD5 = "8c7016b98a9864afb8cc0a7eb8ba62fa"
DEPENDS = "tegra-libraries-core tegra-libraries-eglcore virtual/egl egl-wayland"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "f045a59be314da86667f52db04a399d5a35ef2b84e6bc1fbf8a593721deecdb9"

inherit features_check

REQUIRED_DISTRO_FEATURES = "vulkan opengl"

do_install() {
    install -d ${D}/usr/lib/aarch64-linux-gnu/nvidia
    if ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'true', 'false', d)}; then
        install -m644 ${S}/usr/lib/aarch64-linux-gnu/nvidia/nvidia_icd.json ${D}/usr/lib/aarch64-linux-gnu/nvidia/
        install -d ${D}${sysconfdir}/vulkan/icd.d
        ln -sf /usr/lib/aarch64-linux-gnu/nvidia/nvidia_icd.json ${D}${sysconfdir}/vulkan/icd.d/
    fi
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
FILES:${PN} += "/usr/lib/aarch64-linux-gnu"
