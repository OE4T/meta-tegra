As of 23 Feb 2026, the `master-l4t-r38.4.x` branch supports JetPack 7.1/L4T R38.4.0.

# Changes from L4T R38.2.x/JetPack 7.0

This release introduces support for the Jetson T4000 module. Like R38.2.x, **only** Thor targets are supported. Machine configurations
for the Orin family remain in the tree, but are **not usable and not supported**.

See the release notes in the NVIDIA documentation for this release for information on new and updated features:
* [Jetson Linux R38.4 release notes](https://docs.nvidia.com/jetson/archives/r38.4/ReleaseNotes/Jetson_Linux_Release_Notes_r38.4.pdf)
* [JetPack download page](https://developer.nvidia.com/embedded/jetpack/downloads) (note that this link is not version-tagged)

## BSP changes

* The BSP supports the Jetson T4000, but a machine configuration for it is not yet present in the layer.
* Note that L4T R38.4.x does **not** support Orin hardware. Machine definitions have not been removed from the layer, but they should not be used.

### Flashing process changes

Flashing in Jetson Linux for Thor targets is performed through a "unified" flashing process that is hidden under the L4T initrd/kernel flashing scripts.

For meta-tegra builds, after unpacking your tegraflash tarball, use `initrd-flash` while connected via USB to your Thor device to generate/sign the binary artifacts, stage them to a unified-flash workspace, and run the unified flashing scripts to flash the device. You can use `./initrd-flash --external-only` to re-flash only the external (rootfs) drive, `./initrd-flash -k NAME` to re-flash just the named partition, or `./initrd-flash --qspi-only` to re-flash only the boot firmware in the QSPI flash. The script also takes a `--debug` option for enabling more verbose logging in the unified flashing tool.

See NVIDIA release notes and documentation for more information.

### Kernel changes

The Linux kernel is still taken from the Ubuntu "Noble" release, and is based on Linux 6.8.12. The kernel recipe has been changed to use NVIDIA's GitLab repo directly, instead of the OE4T copy in GitHub.

Please note that the linux-yocto kernel (or any other kernel than the NVIDIA/Ubuntu one) is still **not supported** on Thor hardware.

## JetPack changes

JetPack 7.1 include only minor JetPack upgrades, for the PVA SDK and for VPI.

## DeepStream SDK

No DeepStream SDK update has been issued, but it is unclear whether the existing DS-8.0 SDK is compatible.
