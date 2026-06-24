# Changes from L4T R36.5.0/JetPack 6.2.2 and R38.4.x/JetPack 7.1

This is the first JetPack release to support **both** the Orin family (AGX Orin, Orin NX, Orin Nano, previously
supported in JetPack 6) **and** AGX Thor (T4000, T5000, previously supported only in JetPack 7).

See the release notes in the NVIDIA documentation for this release for information on new and updated features:
* [Jetson Linux R39.2 release notes](https://docs.nvidia.com/jetson/archives/r39.2/ReleaseNotes/Jetson_Linux_Release_Notes_r39.2.pdf)
* [JetPack 7.2 download page](https://developer.nvidia.com/embedded/jetpack/downloads/archive-7.2)

## BSP changes

### New machine configurations

Support for the AGX Thor platform is incorporated alongside Orin. New machine configurations added versus r36.x:
* `jetson-agx-thor-devkit` — AGX Thor development kit (T5000 module, p4071 carrier)
* `jetson-agx-thor-t4000` — AGX Thor T4000 module installed in a p4071 carrier

### Flashing process changes

Only `initrd-flash` is supported for flashing. See the [Flashing](../Flashing.md) page for
more information.

### Security OS changes

OP-TEE gets an update to 4.6 with L4T R39.2. Hafnium (used on Thor only) is also updated.
The Orin and Thor ARM TF-A firmware remains at their same respective base versions (different
between the two platforms) as prior releases.

### UEFI changes

The EDK2/UEFI source base has been updated for L4T R39.2, covering both Orin and Thor
targets. For Orin users that have customized their UEFI configurations, note that
configuration handling for UEFI builds in this release follows the updated
approach used for Thor in R38.4, providing default 'general', 'minimal', and
'simple' configurations in-tree.


## Kernel changes

### Orin (Tegra234)

The kernel for Orin has been upgraded from `linux-jammy-nvidia-tegra` (5.15-based) to
`linux-noble-nvidia-tegra` (6.8.12-based). This is a significant kernel version upgrade.

A notable consequence of moving to the 6.8 kernel is that many Tegra ASoC audio drivers that were
previously delivered as out-of-tree modules (in the `nvidia-kernel-oot-alsa` package) are now
included in the mainline kernel. The following drivers have moved in-tree and are no longer
provided by the OOT package:

* `snd-soc-tegra186-asrc`
* `snd-soc-tegra186-dspk`
* `snd-soc-tegra210-admaif`
* `snd-soc-tegra210-adx`
* `snd-soc-tegra210-ahub`
* `snd-soc-tegra210-amx`
* `snd-soc-tegra210-dmic`
* `snd-soc-tegra210-i2s`
* `snd-soc-tegra210-mixer`
* `snd-soc-tegra210-mvc`
* `snd-soc-tegra210-ope`
* `snd-soc-tegra210-sfc`
* `snd-soc-tegra-machine-driver`

A smaller set of audio drivers (ADSP and virtualization-related) remains out-of-tree.

### Thor (Tegra264)

Thor targets already used `linux-noble-nvidia-tegra` (6.8.12) in the R38.x branches. The kernel
base version is unchanged, but has been updated to the R39.2.0 L4T patch series.

### Out-of-tree drivers

The out-of-tree kernel module package has been updated to R39.2.0, with compatibility updates for
the 6.8 kernel across both Tegra234 and Tegra264 platforms.

## JetPack changes

JetPack 7.2 includes significant component upgrades from JetPack 6.2.2:

| Component | JetPack 6.2.2 | JetPack 7.1 | JetPack 7.2 |
|-----------|---------------|-------------|-------------|
| CUDA      | 12.6          | 13.0        | 13.2        |
| cuDNN     | 9.3.0         | 9.12.0      | 9.20.0      |
| TensorRT  | 10.3.0        | 10.13.3     | 10.16.2     |

PVA SDK and Nsight Systems have also been updated; see the NVIDIA release notes for version details.

## Other new features

## New recipes

### SIPL/UDDF support

Recipes have been added for the SIPL API package and `nvsiplcamerasrc` GStreamer plug-in.
Consult the "Camera Development" chapter of the Jetson Linux Developer Guide for more information.

### Jetson-IO support

The `python3-jetson-io` recipe has been added for installing the Jetson-IO tool
for runtime pin configuration. See [here](../extlinux.conf-support.md(#jetson-expansion-header-configuration-jetson-io)
for more information.

## Container support

The `nvidia-docker` recipe has been removed. The `nvidia-container-toolkit` recipe provides
container runtime support; see [NVIDIA Container Runtime Support](../NVIDIA-Container-Runtime-support.md) for details.

## DeepStream SDK

No information is available yet on DeepStream SDK compatibility with JetPack 7.2.
