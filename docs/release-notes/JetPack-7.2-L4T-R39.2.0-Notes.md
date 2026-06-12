As of June 2026, the `master` branch supports JetPack 7.2/L4T R39.2.0.

# Changes from L4T R36.5.0/JetPack 6.2.2 and R38.4.x/JetPack 7.1

This release is the first JetPack 7 release to support **both** the Orin family (AGX Orin, Orin NX, Orin Nano) **and** AGX Thor (T4000, T5000) simultaneously. Previous JetPack 7.x releases (R38.x) were Thor-only on their respective branches.

See the release notes in the NVIDIA documentation for this release for information on new and updated features:
* [Jetson Linux R39.2 release notes](https://docs.nvidia.com/jetson/archives/r39.2/ReleaseNotes/)
* [JetPack 7.2 download page](https://developer.nvidia.com/embedded/jetpack/downloads/archive-7.2)

## BSP changes

### New machine configurations

Support for the AGX Thor platform is incorporated alongside Orin. New machine configurations added versus r36.x:
* `jetson-agx-thor-devkit` — AGX Thor development kit (T4000 module, p4071 carrier)
* `jetson-agx-thor-t4000` — AGX Thor T4000 module

### Flashing process changes

The `tegra-flash-helper.sh` script has been renamed to `tegra234-flash-helper.sh`, and a new
`tegra264-flash-helper.sh` has been added for Thor targets. Both Tegra234 (Orin) and Tegra264 (Thor)
use the `initrd-flash` flow, but each invokes its own SoC-specific flash helper internally.

For both Orin and Thor targets, use `initrd-flash`. Options such as `--external-only`, `-k NAME`, `--qspi-only`, and `--debug` are available on both platforms.

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

### nvidia-kernel-oot

The out-of-tree kernel module package has been updated to R39.2.0, with compatibility updates for
the 6.8 kernel across both Tegra234 and Tegra264 platforms.

## JetPack changes

JetPack 7.2 includes significant component upgrades from JetPack 6.2.2:

| Component | JetPack 6.2.2 | JetPack 7.1 | JetPack 7.2 |
|---|---|---|---|
| CUDA | 12.6 | 13.0 | 13.2 |
| cuDNN | 9.3.0 | 9.12.0 | 9.20.0 |
| TensorRT | 10.3.0 | 10.13.3 | 10.16.2 |
| Hafnium | — | 2.9 | 2.11 |
| OPTEE | 4.2 | 4.4 | 4.6 |

PVA SDK and Nsight Systems have also been updated; see the NVIDIA release notes for version details.

## New recipes

* **`gstreamer1.0-plugins-nvsiplcamerasrc`** — GStreamer source plugin for SIPL-based cameras,
  built from source. Requires `jetson-sipl-api`.
* **`jetson-sipl-api`** — SIPL camera API library.
* **`edk2-firmware-tegra-rcmboot-prebuilt`** — Optional prebuilt UEFI RCMBoot binary, for users
  who cannot or do not wish to build the EDK2 sources. Set
  `PREFERRED_PROVIDER_edk2-firmware-tegra-rcmboot = "edk2-firmware-tegra-rcmboot-prebuilt"` to
  use it.
* **`tegra-libraries-openwfd`** — OpenWFD display library.

## Container support

`nvidia-docker` has been replaced by `nvidia-container-toolkit`. Add `nvidia-container-toolkit`
to your image instead of `nvidia-docker`. See [NVIDIA Container Runtime Support](../NVIDIA-Container-Runtime-support.md) for details.

## DeepStream SDK

No information is available yet on DeepStream SDK compatibility with JetPack 7.2.
