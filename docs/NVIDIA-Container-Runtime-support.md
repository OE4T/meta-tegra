Notes on integrating the NVIDIA container runtime with Docker support on Jetson platforms.

# Supported branches

Container runtime support is available on the `zeus-l4t-r32.3.1` and later branches.

# Layers required

In addition to the OE-Core and meta-tegra layers, you will need the
[meta-virtualization](https://git.yoctoproject.org/cgit/cgit.cgi/meta-virtualization/) layer and
the `meta-oe`, `meta-networking`, and `meta-python` layers from the
[meta-openembedded](https://git.openembedded.org/meta-openembedded/) repository.

# Configuration

Add `virtualization` to your `DISTRO_FEATURES` setting.

# Building

1. Add `nvidia-container-toolkit` to your image to enable GPU-accelerated containers.

   **Note:** The `nvidia-docker` recipe was removed in L4T R39.2.0/JetPack 7.2.
   `nvidia-container-toolkit` is the current replacement and provides the same functionality.

2. See the [NVIDIA Container Toolkit documentation](https://docs.nvidia.com/datacenter/cloud-native/container-toolkit/latest/install-guide.html)
   for details on runtime configuration and usage.

3. The [Docker containers that NVIDIA supplies](https://ngc.nvidia.com/catalog/containers?orderBy=modifiedDESC&pageNumber=0&query=jetson&quickFilter=containers&filters=)
   do not bundle most hardware-specific libraries, but expect them to be provided by the host OS.
   Be sure to include TensorRT, cuDNN, and/or other JetPack components in your image if you expect
   to run containers that need them.

4. For containers that use GStreamer, include the Jetson-specific GStreamer plugins you may need.
   See [Tegra-specific GStreamer plugins](Tegra-specific-gstreamer-plugins.md) for the available
   plugin recipes.
