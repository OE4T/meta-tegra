This page documents an **EXPERIMENTAL** feature for improving the
reliability of any over-the-air update process which performs combined
rootfs and capsule (boot firmware) updates.

See [this page](UEFI-Capsule-Update.md) for more information on capsule
updates, and for information on planning upgrades when changing between the
standard UEFI build and the minimal build enabled by this feature.

Use with caution.

# Quick start

To enable the minimal UEFI configuration:

    TEGRA_MINIMAL_BOOT = "1"

This would normally be set in your machine configuration file. If you
set this elsewhere (e.g., in your `local.conf`), and want it to apply
to just one machine, you could use the `:<machine-name>` override
on the variable.

# What this does

1. Selects an appropriate "simple" configuration for the UEFI build, based
   on the boot device (from the `TNSPEC_BOOTDEV` variable), so only
   drivers needed for booting from that device are included.

2. Disables other unneeded UEFI features, including the video display.

3. Builds the L4TLauncher application, which normally gets built as a
   separate EFI application binary and stored in the ESP partition,
   directly into the EFI image.

3. Patches the PlatformBootOrderLib library to boot from the built-in
   L4TLauncher application, but also make the ESP partition accessible,
   so capsule updates can be applied.

With this configuration, no `BOOTAA64.EFI` file is required in the ESP
partition. Instead, the embedded L4TLauncher application is used to
load and start the Linux kernel.

Boot speed is also improved, since the UEFI image is smaller, and only
one boot path is probed.

The configuration settings also disable the use of the Android-style kernel
and device tree partitions, so you should use the settings to create an
[extlinux.conf file](extlinux.conf-support.md) for populating `/boot`
with your kernel/device tree/etc. This also allows you to customize your
storage layout to remove the two kernel and two device tree partitions,
which are there for Android-style boot support only.

# Why do it

The one flaw in the otherwise fully redundant A/B boot chain support
present on Jetson systems is the lack of redundancy in the EFI System
Partition. Power failures while updating the `BOOTAA64.EFI` boot
launcher application on the ESP would render the system unbootable,
since there would be no OS launcher available. And since the interface
between UEFI and the boot launcher can change from release to release,
particularly when using NVIDIA's `L4TLauncher`, a boot firmware
update _must_ be accompanied by an update to the launcher application.

Some workarounds for this shortcoming have been developed, such as
having a second partition for to hold the updated OS launcher and
swapping the partition type GUIDs between the active ESP and secondary
ESP during an update. Most, if not all, workarounds still leave some
window of time during which they are vulnerable to power failures.

By building the launcher directly into the EFI image itself, no ESP
update is ever required. The ESP is still present, and is used to
store update capsules, but it does not need to be atomically updated.
