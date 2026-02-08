Bootloader signing is supported for all Jetson targets for which secure boot is available (consult the L4T documentation). Support was added in the `zeus` branch for tegra186 (Jetson-TX2), and extended to the other SoC types in the `dunfell-l4t-r32.4.3` branch.

**Note** that with L4T R35.2.1 and later, the secure boot sequence has changed.  See [this page](https://github.com/OE4T/meta-tegra/wiki/Secure-Boot-Support-in-L4T-R35.2.1-and-later) for more information.

## Setting fuses for secure boot ##
To enable secure boot on your device, follow the instructions in the [L4T BSP documentation](https://docs.nvidia.com/jetson/l4t/index.html) and the README included in the L4T Secure Boot package that can be downloaded [here](https://developer.nvidia.com/embedded/linux-tegra).

## Caveats ##
* The `odmfuse.sh` script in some L4T releases has a bug that causes fusing to fail on Jetson-TX2 devices; see [issue #193](https://github.com/madisongh/meta-tegra/issues/193) for an explanation and patch.
* The L4T bootloader for tegra210 (TX1/Nano) has a bug that always **disables** secure boot during fuse burning in versions of L4T prior to R32.4.4.  See [this NVIDIA Developer Forum post](https://forums.developer.nvidia.com/t/144888/27) for more information, and patched copies of the bootloader with a fix.
* NVIDIA does **not** support secure boot on SDcard-based developer kits (Jetson Nano/Nano-2GB and Jetson Xavier NX). *You may
render your developer kit* **permanently unbootable** *if you attempt to burn the secure boot fuses*.
* The tools and scripts in L4T for secure boot support do not appear to be very well tested from release to release, and occasionally regressions get introduced that break fuse burning for some of the Jetson platforms, so be very careful when updating to a new release of the BSP.

## Enabling boot image and BUP signing during the build ##
If you have the signing and (optional) encryption key files available, you can add the following setting to your `local.conf` to create signed boot images and BUP packages:
```
TEGRA_SIGNING_ARGS = "-u /path/to/signing-key.pem -v /path/to/encryption-key"
```
The additional arguments will be passed through to the flash-helper script and all files will be signed (and boot files will be encrypted, if the `-v` option is provided) during the build. The `doflash.sh` script in the resulting `tegraflash` package will flash the signed files to the devices.  This is similar to the `flashcmd.txt` script you would get if you used the L4T `flash.sh` script with the `--no-flash` option as mentioned in the NVIDIA secure boot documentation.

### Kernel and DTB encryption ###
Starting with L4T R32.5.0, cboot on tegra186 (TX2) and tegra194 (Xavier) platforms expect the kernel (boot.img) and kernel device tree
to be encrypted as well as signed. This encryption is performed by a service in Trusty and uses a different encryption key than the one
used for encrypting the bootloaders.  See the L4T documentation for information on setting this up.

If you have set up kernel/DTB encryption on your device, add `--user_key /path/to/kernel-encryption-key` to `TEGRA_SIGNING_ARGS`.
If you do not go through the extra steps of setting up a kernel encryption key, an all-zeros key will be used by default.

## Manual signing ##
If you prefer not to have the signing occur during your build, you can manually add the necessary arguments to your invocation of `doflash.sh` after unpacking the `tegraflash` package.  For example:
```
$ BOARDID=<boardid> FAB=<fab> BOARDSKU=<boardsku> BOARDREV=<boardrev> ./doflash.sh -u /path/to/signing-key.pem -v /path/to-encryption-key
```
The environment variable settings you need on the command will vary from target to target; consult the "Signing and Flashing Boot Files" section of the [L4T BSP documentation](https://docs.nvidia.com/jetson/l4t/index.html) for the specifics.

With recent branches, BUP generation can also be performed manually.  The `tegraflash` package includes a `generate_bup_payload.sh` script that can be run with the same `-u` (and, if applicable `-v`) options to generate a BUP payload manually.

## Using a code signing server ##
If you prefer not to have your signing/encryption keys local to your development host, you can override the `tegraflash_custom_sign_pkg` and `tegraflash_custom_sign_bup` functions in `image_types_tegra.bbclass` to package up the files in the current working directory, send them to be signed, then unpack the results back into the current directory. Everything needed to perform the signing, except for the keys, will be present in the package sent to the server. An example implementation of a code signing server is available [here](https://github.com/madisongh/digsigserver).