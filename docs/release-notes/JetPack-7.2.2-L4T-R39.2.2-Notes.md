# Changes from L4T R39.2.1/JetPack 7.2.1

See the release notes in the NVIDIA documentation for this release for information on new and updated features:
* [Jetson Linux R39.2.2 release notes](https://docs.nvidia.com/jetson/archives/r39.2.2/ReleaseNotes/Jetson_Linux_Release_Notes_r39.2.2.pdf)
* [JetPack 7.2.2 download page](https://developer.nvidia.com/embedded/jetpack/downloads/archive-7.2.2)

## BSP changes

This release mainly contains bugfixes. The driver library version moves from
595.78 to 595.113.

See NVIDIA release notes for information on other changes/improvements.

### Kernel changes

The kernel version remains at 6.8.12 for both Orin and Thor. The
Noble-based kernel moves from Ubuntu 6.8.0-1021.21 to 6.8.0-1029.30.

* `KERNEL_ARGS` are aligned with L4T R39.2.2. Orin adds `swiotlb=2048` and
  ramoops-backed pstore (`efi_pstore.pstore_disable=1 pstore.backend=ramoops`),
  and tegra234 machines now install `kernel-module-ramoops`. Thor adds
  `swiotlb=2048` and `nvme.poll_queues=4`, and drops `audit=1`.
* The devkit Realtek Bluetooth support uses the in-kernel `btrtlusb` module
  instead of the out-of-tree `rtk-btusb` module.
* AF_XDP is now enabled for both kernels: `linux-yocto` gains it in this
  release, matching `linux-noble-nvidia-tegra`, which already had it enabled.
* The out-of-tree Security Engine modules (`tegra-se`, `tegra-se-kds` and
  `tegra-se-nvrng`) are no longer installed by default, matching L4T, which
  denylists them. They are still built and can be added to an image
  explicitly.

### UEFI changes

The EDK2/UEFI sources are updated to the L4T R39.2.2 tags. NVIDIA's edk2
tree is now based on edk2-stable202511 instead of edk2-stable202505, and
the EDK2 CVE metadata is updated to match.

* NVIDIA's sources now include the PXE ICMP buffer overflow fix for
  CVE-2026-97631, so meta-tegra no longer carries its own backport. The
  related PXE tests and the ICMP error buffer fix are still applied.

### Recipe changes

* Vulkan SC: L4T now provides only the closed-source pieces, the NVIDIA ICD
  (`tegra-vulkan-sc-icd`) and the pipeline cache compiler (`tegra-vulkan-sc-pcc`).
  The components that are available as source, and the Vulkan SC samples, have
  moved to meta-tegra-community. The JSON generation layer is no longer
  installed, as the prebuilt library requires the OpenSSL 3 ABI.
* `libnvidia-rmapi-tegra` and `libnvidia-tls` are now packaged in
  `tegra-libraries-eglcore`, following their move to `nvidia-l4t-3d-core`.
* `libv4l2-minimal` is updated to the v4l-utils 1.32.0 base.
* `tegra-libraries-winsys` and `libv4l2-nvvidconv-wrapper` are removed; no
  L4T R39.2.x package ships the content they provided.
* New `nvidia-video-codec-sdk` recipe (13.0.37) for the NVENC/NVDEC headers
  and samples. The codec libraries are available on Thor only.
* nvpmodel: the vendor `nvpmodel.sh` wrapper is installed and used, so the
  Orin GPU udev handler selects the `nvhost_podgov` governor.
* `tegra-nvsciipc`: fixed the udev match, and `nvsciipc_init` runs as the
  `nvsciipc` user.
* Jetson SIPL API packages the R39.2.2 camera modules, including the ISX031
  and VB1940 query libraries.

## JetPack changes

TensorRT moves to a new major version, and some other components received
micro-version bumps:

| Component      | JetPack 7.2.1   | JetPack 7.2.2   |
|----------------|-----------------|-----------------|
| TensorRT       | 10.16.2.10      | 11.4.1.31       |
| VPI            | 4.1.4           | 4.1.6           |
| Nsight Systems | 2026.3.1.157    | 2026.5.1.161    |

CUDA (13.2.2), cuDNN (9.20.0.46) and PVA SDK (2.9.4) are unchanged.

### TensorRT 11

* The runtime packages are named for soname 11, and TensorRT 11 no longer
  ships static libraries.
* `tensorrt-samples` and `tensorrt-trtexec` are updated to the TensorRT 11.4
  OSS release. They now live in `external/openembedded-layer` and are only
  available when meta-oe is in the layer stack.
* DLA support for Orin: new `tensorrt-dla-compiler` and `libcudla` recipes,
  and `sample_cudla` is built on tegra234.
