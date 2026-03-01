As of 12 Nov 2025, the `master-l4t-r38.2.x` branch supports JetPack 7.0/L4T R38.2.2. Note that R38.2.2 only updates some of the components; the rest remain at 38.2.1.

# Changes from L4T R36.4.4/JetPack 6.2.1

This release introduces support for the Jetson AGX Thor development kit, and supports **only** AGX Thor targets. Machine configurations
for the Orin family remain in the tree, but are **not usable and not supported**.

See the release notes in the NVIDIA documentation for this release for information on new and updated features:
* [Jetson Linux R38.2 release notes](https://docs.nvidia.com/jetson/archives/r38.2/ReleaseNotes/Jetson_Linux_Release_Notes_r38.2.pdf)
* [Jetson Linux R38.2.1 release notes](https://docs.nvidia.com/jetson/archives/r38.2.1/ReleaseNotes/Jetson_Linux_Release_Notes_r38.2.1.pdf)
* [JetPack 7.0 download page](https://developer.nvidia.com/embedded/jetpack/downloads) (note that this link is not version-tagged)

Note that NVIDIA has not provided release notes for R38.2.2.

## BSP changes

* Many changes are present to support Thor hardware. Note that L4T R38.2.x does **not** support Orin hardware. Machine definitions have not been removed from the layer, but they should not be used.

### Flashing process changes

Flashing in Jetson Linux for Thor targets is performed through a "unified" flashing process that is hidden under the L4T initrd/kernel flashing scripts.

For meta-tegra builds, after unpacking your tegraflash tarball, use `initrd-flash` while connected via USB to your Thor device to generate/sign the binary artifacts, stage them to a unified-flash workspace, and run the unified flashing scripts to flash the device. You can use `./initrd-flash --external-only` to re-flash only the external (rootfs) drive, `./initrd-flash -k NAME` to re-flash just the named partition, or `./initrd-flash --qspi-only` to re-flash only the boot firmware in the QSPI flash. The script also takes a `--debug` option for enabling more verbose logging in the unified flashing tool.

See NVIDIA release notes and documentation for more information.

### Kernel changes

The Linux kernel is now taken from the Ubuntu "Noble" release, and is based on Linux 6.8.12.

## JetPack changes

JetPack 7.0 upgrades most of the JetPack content. See the NVIDIA documentation for more information.

CUDA 13, included in JetPack 7.0, supports the use of gcc/g++ 15 as the host toolchain, so the `gcc-for-nvcc` recipes, which were used for providing an older toolchain for use with `nvcc`, have been dropped.

## DeepStream SDK

DeepStream 8.0 is available. The recipes for DeepStream in the meta-tegra-community layer have been updated.
