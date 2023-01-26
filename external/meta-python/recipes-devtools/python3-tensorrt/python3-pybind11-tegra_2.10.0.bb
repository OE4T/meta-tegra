require recipes-devtools/python/python3-pybind11_2.10.0.bb

SRC_URI += "file://0001-Fix-casts-to-void-4275.patch"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
