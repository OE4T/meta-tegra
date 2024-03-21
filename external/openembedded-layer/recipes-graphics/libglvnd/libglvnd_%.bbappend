PROVIDES:append:tegra = " virtual/egl virtual/libgl virtual/libgles1 virtual/libgles2 virtual/libgles3"

DEPENDS:append:tegra = " l4t-nvidia-glheaders"

PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"

PACKAGECONFIG:tegra ?= "egl gles1 gles2 ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11 glx', '', d)}"

RPROVIDES:${PN}:append:tegra = " libegl libgl libgles1 libgles2"
RPROVIDES:${PN}-dev:append:tegra = " libegl-dev libgl-dev libgles1-dev libgles2-dev libgles3-dev"
RCONFLICTS:${PN}:tegra = "libegl libgl ligbles1 libgles2"
RCONFLICTS:${PN}-dev:append:tegra = " libegl-dev libgl-dev libgles1-dev libgles2-dev libgles3-dev"
RREPLACES:${PN}:tegra = " libegl libgl libgles1 ligbles2"
RREPLACES:${PN}-dev:append:tegra = " libegl-dev libgl-dev libgles1-dev libgles2-dev libgles3-dev"

RDEPENDS:${PN}:append:tegra = " tegra-libraries-eglcore tegra-libraries-glescore ${@bb.utils.contains('PACKAGECONFIG', 'x11', 'tegra-libraries-glxcore', '', d)}"
RDEPENDS:${PN}-dev:append:tegra = " l4t-nvidia-glheaders-dev"
RRECOMMENDS:${PN}:append:tegra = " ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'egl-wayland', '', d)}"
