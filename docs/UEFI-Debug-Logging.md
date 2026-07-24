# UEFI Debug Logging

To enable more verbose logging in the UEFI bootloader, which
can help with troubleshooting boot issues, set

    TEGRA_UEFI_VERBOSE_LOGGING = "1"

in your `local.conf` file. This adds a configuration fragment
to the build that the NVIDIA Kconfig/Kbuild system uses to enable
more extensive logging in what would otherwise be a release-type
build.
