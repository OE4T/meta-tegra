# Tegra-specific GStreamer plugins

Starting with L4T R35.x (JetPack 5), the NVIDIA-specific GStreamer plugins are built individually
from source as separate recipes, rather than being delivered as a single binary blob package.
Each recipe below corresponds to a Yocto package that can be added to your image.

## Plugin recipes

| Recipe | GStreamer element(s) | Description |
|---|---|---|
| `gstreamer1.0-plugins-nvarguscamerasrc` | `nvarguscamerasrc` | LibArgus-based CSI camera capture source |
| `gstreamer1.0-plugins-nvcompositor` | `nvcompositor` | GPU-accelerated multi-input compositor |
| `gstreamer1.0-plugins-nvdrmvideosink` | `nvdrmvideosink` | DRM/KMS video sink |
| `gstreamer1.0-plugins-nveglgles` | `nvvideosink`, `nvoverlaysink` | EGL/GLES-based render sinks |
| `gstreamer1.0-plugins-nvipcpipeline` | `nvipcsrc`, `nvipcsink` | Inter-process GStreamer pipeline elements |
| `gstreamer1.0-plugins-nvjpeg` | `nvjpegdec`, `nvjpegenc` | Hardware-accelerated JPEG decode/encode |
| `gstreamer1.0-plugins-nvsiplcamerasrc` | `nvsiplcamerasrc` | SIPL-based camera source (R39.2.0+, requires `jetson-sipl-api`) |
| `gstreamer1.0-plugins-nvtee` | `nvtee` | Tegra-specific tee element |
| `gstreamer1.0-plugins-nvunixfd` | `nvunixfdsrc`, `nvunixfdsink` | Unix file-descriptor buffer sharing elements |
| `gstreamer1.0-plugins-nvv4l2camerasrc` | `nvv4l2camerasrc` | V4L2-based camera capture source |
| `gstreamer1.0-plugins-nvvidconv` | `nvvidconv` | Hardware video format and color-space conversion |
| `gstreamer1.0-plugins-nvvideo4linux2` | `nvv4l2dec`, `nvv4l2h264enc`, `nvv4l2h265enc`, etc. | V4L2-based hardware video decode and encode |
| `gstreamer1.0-plugins-nvvideosinks` | `nvvideosink` | NvBuf-backed video sink |

### Supporting packages

* **`libgstnvcustomhelper`** — shared helper library used internally by several of the plugins above; pulled in automatically as a dependency
* **`nvgstapps`** — sample GStreamer applications from NVIDIA demonstrating use of these plugins

## Machine configuration

The `MACHINE_HWCODECS` variable in each machine configuration selects the hardware codec packages
for that target. The GStreamer plugins above can be included individually in your image recipe, or
grouped through appropriate `IMAGE_INSTALL` or `MACHINE_EXTRA_RRECOMMENDS` settings in your
layer.

## Camera source plugin notes

**nvarguscamerasrc** is the standard CSI camera source for most Jetson platforms, using the
LibArgus camera API.

**nvsiplcamerasrc** (available from R39.2.0/JetPack 7.2 onwards) is an alternative source for
cameras attached via the SIPL framework. It is built from source and depends on the
`jetson-sipl-api` package. SIPL is typically used with more complex camera module configurations
that require an ISP tuning database.
