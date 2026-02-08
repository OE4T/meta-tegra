Originally, the machine configurations set MACHINE_GSTREAMER_1_0_PLUGIN to include the `gstreamer1.0-plugins-tegra` package, which is the base set of binary-only gstreamer plugins that is provided with L4T.  In more recent releases, this has been changed to point to `gstreamer1.0-omx-tegra` instead (and using the now-current MACHINE_HWCODECS variable) to make it easier to build multimedia-ready images.

Note that since the OpenMAX plugins package is flagged as commercially licensed, it is also whitelisted in the machine configuration with:
```
LICENSE_FLAGS_WHITELIST_append = " commercial_gstreamer1.0-omx-tegra"
```

## Update 2020-09-17
Starting with the branches using L4T R32.4.3 (`dunfell-l4t-r32.4.3` and later), the commercially-licensed flag was removed from the OpenMAX plugin recipe, as the sources are available and do not appear to contain any encumbered code.

