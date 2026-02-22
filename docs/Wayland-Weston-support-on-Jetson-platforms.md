Support for Wayland/Weston has been adapted from the open-source libraries and patches that NVIDIA has published, rather than using the binary-only libraries packaged into the L4T BSP.

## DRM/KMS support
Starting with L4T R32.2.x, DRM/KMS support in the BSP is provided through a combination of a custom
`libdrm.so` shared library and the `tegra-udrm` kernel module. The library intercepts some DRM API calls;
any APIs it does not handle directly are passed through to the standard implementation of `libdrm`.

Builds that include `weston` will also include a configuration file (via the `tegra-udrm-probeconf` recipe)
that loads the `tegra-udrm` module with the parameter `modeset=1`. This enables KMS support in the
L4T-specific `libdrm` library. If your build includes a different Wayland-based compositor, you may also
need to include this configuration file.

(Earlier versions of L4T used a different custom `libdrm` implementation that had no KMS support and was
not ABI-compatible with the standard `libdrm` implementation.)

## Mesa build changes
The Mesa build has been changed to enable `libglvnd` support, which creates the necessary vendor plugins of the EGL and GLX libraries and packages them as `libegl-mesa` and `libgl-mesa`.

## xserver-xorg changes
The xserver-xorg build has also been changed to disable DRI and KMS support on Tegra platforms.

## libglvnd
Starting with L4T R32.1, the BSP uses [libglvnd](https://gitlab.freedesktop.org/glvnd/libglvnd) rather than including pre-built copies of the OpenGL/EGL/GLES libraries.

## egl-wayland
The [egl-wayland](https://github.com/NVIDIA/egl-wayland) extension is built from source, with an additional patch to correct an issue with detecting Wayland displays and surfaces. The recipe also installs the needed JSON file so that the extension can be found at runtime.

## weston-eglstream
NVIDIA's patches for supporting Weston using the EGLStream/EGLDevice backend are maintained
[in this repository](https://gitlab.freedesktop.org/ekurzinger/weston). As of L4T R32.2.x, no additional
Tegra-specific patches are required.

The `--use-egldevice` option gets added to the command line when starting Weston to activate this support.

Note that support for the EGLStream backend was dropped in Weston 10 in favor of using GBM. We supply a backend for `libgbm` that uses
NVIDIA's `libnvgbm.so` manage GBM objects, and we still patch Weston support the EGLStream protocol for Wayland clients.

## XWayland
XWayland appears to work, but hardware-accelerated OpenGL (through the `libGLX_nvidia` provider) is _not_ available.

## Testing
The following tests are performed:
1. Verify that `core-image-weston` builds.
2. Verify that weston starts at boot time.
3. Verify that weston sample programs, such as `weston-simple-egl`, display appropriate output.
4. Verify that the `nveglglessink` gstreamer plugin works with the `winsys=wayland` parameter by running a gstreamer pipeline to display an H.264 video. Note that the `DISPLAY` environment variable must not be set, per the NVIDIA documentation.
5. Verify that the `l4t-graphics-demos` applications work.

## Troubleshooting

The following commands work on a Jetson TX2 and probably others:

### Turn off HDMI:
```
echo -1 > /sys/kernel/debug/tegra_hdmi/hotplug
echo 4 > /sys/class/graphics/fb0/blank
```
([Source](https://gist.github.com/tstellanova/818a0d4533406b519df4#turn-off-hdmi))

### Turn on HDMI:
```
echo 1 > /sys/kernel/debug/tegra_hdmi/hotplug
echo 0 > /sys/class/graphics/fb0/blank
```
([Source](https://gist.github.com/tstellanova/818a0d4533406b519df4#turn-on-hdmi))

### Reading HDMI connection state:
`/sys/devices/virtual/switch/hdmi/state` is `0` when disconnected and `1` when connected. ([Source](https://forums.developer.nvidia.com/t/tx2i-tegra-udrm-hdmi-connection-status/110612/2))