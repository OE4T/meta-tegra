Notes on integration of the Jetson-customized NVIDIA container runtime (beta version 0.9.0) with Docker support.
See [this page](https://github.com/NVIDIA/nvidia-docker/wiki/NVIDIA-Container-Runtime-on-Jetson) for information on how this is integrated with the JetPack SDK.

# Supported branches
Support for the container runtime is available on the `zeus-l4t-r32.3.1` and later branches.

# Layers required
In addition to the OE-Core and meta-tegra layers, you will need the  [meta-virtualization](https://git.yoctoproject.org/cgit/cgit.cgi/meta-virtualization/) layer and the `meta-oe`, `meta-networking`, and `meta-python` layers from the [meta-openembedded](https://git.openembedded.org/meta-openembedded/) repository.

# Configuration
Add `virtualization` to your DISTRO_FEATURES setting.

# Building
1. To run any containers, add `nvidia-docker` to your image.
2. The [Docker containers that NVIDIA supplies](https://ngc.nvidia.com/catalog/containers?orderBy=modifiedDESC&pageNumber=0&query=jetson&quickFilter=containers&filters=) do not bundle in most of the hardware-specific libraries needed to run them, but expect them to be provided by the underlying host OS, so be sure to include TensorRT ([note](L4T-R32.3.1-Notes.md#tensorrt-packaging-change)), CuDNN, and/or VisionWorks, if you expect to be running containers needing those packages.
3. For containers that use GStreamer, be sure to include the Jetson-specific GStreamer plugins you may need.
