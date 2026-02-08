# Update: 10 Feb 2025

In the `master` branch:
* The image type for tegraflash packages has been changed to `tegraflash.tar`.
* The `zip` format for tegraflash packages has been removed. Zip packages do not work well with Linux sparse files, which are used for the EXT4 filesystem images we include in the package.
* The default for `IMAGE_FSTYPES` is now set to `tegraflash.tar.zst`, using zstd compression on the package, which provides good compression with much faster compression and decompression times than gzip. You can override this in your build configuration, if needed.

# Update: 27 May 2020
As of 27 May 2020, the `image_types_tegraflash.bbclass` and the helper scripts have
been enhanced in the branches that support L4T R32.3.1 and later (zeus-l4t-r32.3.1, dunfell,
dunfell-l4t-r32.4.2, and master). The sections below describe these updates.

# Compressed-tar instead of zip for packaging
The venerable zip archive format has worked well enough over the years,
but the zip tools are quite old and don't have support for modern features
like parallelism and sparse files. Switching to using a compressed tarball
for tegraflash packages substantially speeds up build times and preserves
sparse-file attributes for EXT4 filesystem images, resulting in much smaller
(actual size vs. apparent size) packages.

In the zeus-l4t-r32.3.1 and dunfell branches, the default packaging remains `zip`.
In dunfell-l4t-r32.4.2 and master, the default packaging has been changed to `tar`.
You can set the variable `TEGRAFLASH_PACKAGE_FORMAT` in your build configuration
to set the package format you want to use.  Note however, that `zip` format is deprecated
and support for it will likely be removed in a future release.

# Use of bmaptool for SDcard creation
If you have the `bmaptool` package installed on your development host, the `make-sdcard`
script will use it in place of `dd` to copy the EXT4 filesystem into the APP partition
of an SDcard, which (when combined with the `tar` packaging mentioned above) results in
much faster SDcard writing.

To take advantage of this, make sure `bmaptool` is available on your PATH and specify the
device name of your SDcard writer when running `dosdcard.sh`.  For example:
```
$ ./dosdcard.sh /dev/sda
```
The device name will be passed through to the underlying `make-sdcard` script.  (If you
run into permissions problems, you may need to use `sudo`.)

# BUP payload generation
If you need to create BUP payloads outside of your bitbake builds, the tegraflash
package now includes all of the files needed to do so, including a script to create
the payload (similar to the `l4t_generate_soc_bup.sh` script in L4T):
```
$ ./generate_bup_payload.sh
```
You can pass the `-u` and/or `-v` options to this script to specify the public and/or private
keys for signing the payload contents if your devices are fused for secure boot, and they
will be passed through to each invocation of the flash helper script.