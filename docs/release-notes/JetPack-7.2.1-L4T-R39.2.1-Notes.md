# Changes from L4T R39.2.0/JetPack 7.2

See the release notes in the NVIDIA documentation for this release for information on new and updated features:
* [Jetson Linux R39.2.1 release notes](https://docs.nvidia.com/jetson/archives/r39.2.1/ReleaseNotes/Jetson_Linux_Release_Notes_r39.2.1.pdf)
* [JetPack 7.2.1 download page](https://developer.nvidia.com/embedded/jetpack/downloads/archive-7.2.1)

## BSP changes

This release mainly contains bugfixes.

See NVIDIA release notes for information on other changes/improvements.

### Kernel changes

The kernel version remains at 6.8.12 for both Orin and Thor, but the out-of-tree module
sources have moved to the `jetson_39.2.1` tag, picking up the R39.2.1 L4T patch series
(nvgpu, nvdisplay, hwpm, and device tree sources all updated).

## JetPack changes

No new JetPack SDK features. Some components received micro-version bumps as part of the
R39.2.1 BSP:

| Component | JetPack 7.2 | JetPack 7.2.1 |
|-----------|-------------|----------------|
| CUDA      | 13.2.1      | 13.2.2         |
| CUDA driver | 595.58.03 | 595.71.05      |
| VPI       | 4.1.3       | 4.1.4          |
| PVA SDK   | 2.9.1       | 2.9.4          |

## DeepStream SDK

DeepStream 9.1 targets JetPack 7.2 GA (r39.2 BSP) and remains compatible.
