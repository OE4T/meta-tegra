# UEFI Capsule Update

For more general information on UEFI capsules, see the "UEFI Adaptation" and "Update and Redundancy" sections
of the Bootloader chapter in the version of the Jetson Linux Developer Guide for the version of Jetson Linux
you are using (archives [here](https://docs.nvidia.com/jetson/archives/)), or visit the [Tianocore EDK II
documentation](https://www.tianocore.org/tianocore-wiki.github.io/).

## FMP System Image Type

The Firmware Management Protocol (FMP), which implements capsule updates, selects a firmware update capsule based on a system
image type GUID. For Tegra UEFI builds, the `CONFIG_FMP_SYSTEM_IMAGE_TYPE_ID` Kconfig setting is used to set the value
that FMP should look for. When an update capsule is created, the `--guid` option for the `GenerateCapsule.py` script
is used to set the system type ID for the firmware contained in the capsule. A capsule update will be applied _only_ if
that GUID matches the system type GUID built into the currently running firmware.

In the initial Jetson Linux UEFI releases (L4T R35.x/R36.x), one system type GUID (per SoC type) was set in the UEFI build configuration,
and the same GUID was used during capsule generation. However, The EFI and NVIDIA documentation recommend that vendors
set unique system type GUIDs for their products, and with L4T R38.x and later, NVIDIA sets a different GUID in each of the pre-defined UEFI
configurations they provide in pre-built form.

To provide a custom system image type GUID for your target system, you can set the `TEGRA_UEFI_SYSTEM_IMAGE_TYPE_GUID` variable
in your machine configuration. When that variable is set to a non-null string, the `edk2-firmware-tegra` recipe automatically creates a configuration
fragment to set `CONFIG_FMP_SYSTEM_IMAGE_TYPE_ID` to the specified string value. The recipe also deploys a file containing the
system image type GUID that was configured into the build, so the `tegra-uefi-capsules` recipe can pass that same GUID to the `GenerateCapsule.py`
script.

### Upgrade Considerations

If you have devices you have already deployed with UEFI firmware you built using older versions of L4T, and you intend
to use a capsule update as part of a field (OTA) upgrade to L4T R39.x or later, make sure you have plan for any system image type ID
change that may occur as part of the upgrade.

The R39.x GUID for the `t23x_general`/`t26x_general` full-featured UEFI configuration is used by default, and matches the
corresponding `general` configuration used in prior releases. If you intend to switch to one of the minimal-boot configurations
described on [this page](UEFI-Minimal-for-A-B-Updates.md) as part of the upgrade, the GUID in the new configuration
will be different, and you must take one of the following steps to provide a compatible upgrade path.

#### Option 1: Use the same image type GUID

The simpler option is to paste the old GUID into the variable setting in your machine configuration, so it matches. This
also simplifies the downgrade path, if you intended to allow for downgrades across a major L4T version boundary using
OTA packages you may have already created based on the older L4T release.

* For t23x (Orin) systems: set `TEGRA_UEFI_SYSTEM_IMAGE_TYPE_GUID = "bf0d4599-20d4-414e-b2c5-3595b1cda402"`
* For t26x (Thor) systems: set `TEGRA_UEFI_SYSTEM_IMAGE_TYPE_GUID = "3c834404-d2d7-4912-8c1c-6e850b5f2824"`

#### Option 2: Use multiple capsules with different image type GUIDs

A more complicated option is to perform an OTA update that supports upgrading the old firmware image to the new one, as well
as updates to the new firmware that uses the new type ID. To do this, set the `TEGRA_UEFI_CAPSULE_ALT_IMAGE_TYPE_GUIDS`
variable to one or more GUID values (separated by blanks if more than one). When set, the `tegra-uefi-capsules` recipe
will generate capsules with the specified GUIDs in the capsule header, each containing the same content as the capsule that
matches the image type GUID specified during the UEFI build. The additional capsules will be named `tegra-bl-<GUID>.cap`
(and likewise for a `-kernel` capsule, if one is generated) where `<GUID>` is the image type GUID value specified in the variable.

You can then create an OTA update package specifically for upgrading from the older release to your new release which bundles in
the capsule that would be recognized by the older firmware.  Alternatively, you could bundle all of the capsules into
the update package and install the correct capsule into the ESP partition (but you would need to identify a method for
determining the correct capsule). With all capsules included, the same update package could be used for updates from
your pre-R39.x _or_ your latest R39.x+ releases.

Be sure to test thoroughly if you adopt this approach, since the typical failure will be that no capsule is selected at all,
which could render a device unbootable. Troubleshooting such issues can be difficult, too.

#### Option 3: Re-flash

A third option is to require re-flashing, rather than attempting to support the change with an OTA update.

## Rollback protection

By default the `LowestSupportedVersion` field in UEFI Capsule FMP Payload Header is set to the current L4T version, which prevents bootloader downgrade.
`TEGRA_UEFI_LOWEST_SUPPORTED_VERSION` variable can be used to override this behaviour.

For example to allow downgrading to L4T 36.4.3 add the following line to your machine config:
```
TEGRA_UEFI_LOWEST_SUPPORTED_VERSION = "0x00240403"
```
