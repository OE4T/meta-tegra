# Use this class only when creating a bbappend for
# a recipe in another layer to introduce a dependency
# on gl/egl/gles/etc. for tegra platforms, when the
# base recipe does not already inherit features_check
# for other purposes.

inherit features_check

REQUIRED_DISTRO_FEATURES:append = " opengl"
