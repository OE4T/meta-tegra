# Disk encryption for Jetson devices

This page describes disk-encryption workflows for meta-tegra. The automated
`initrd-flash` workflow is supported on Jetson Orin (Tegra234) only. Jetson
AGX Thor (Tegra264) continues to use the manual post-build workflow below.

Neither workflow manages production secrets. Generating fuse keys, the EKB,
and per-device LUKS passphrase files belongs to the product flashing process.

## Orin: encrypted `initrd-flash` partitions

Mark every partition that must be encrypted in its partition layout XML:

```xml
<partition name="APP_ENC" type="data" encrypted="true">
```

When `initrd-flash` exports the target storage as a block device, its host-side
`make-sdcard` script detects this attribute. It formats that partition as LUKS2,
opens it temporarily, and writes the partition image to the opened mapper. The
script closes the mapper after writing it.

This applies only when flashing a block device via `initrd-flash`. Encrypted
partitions are intentionally not supported when `make-sdcard` creates a regular
`.sdcard` image file.

### Host requirements

The flashing host needs `cryptsetup`, in addition to the normal initrd-flash
host tools. The tegraflash package includes these NVIDIA scripts:

```
tools/disk-encryption/gen_ekb.py
tools/disk-encryption/gen_luks_passphrase.py
```

The package does not include Python or Python's `cryptography` module. Those
are host dependencies when either script is run.

### Per-partition inputs

Before running `initrd-flash`, export one key-file variable and one UUID
variable for every encrypted partition. The variable suffix is the partition
name uppercased with every non-alphanumeric character replaced by `_`.

For example, `APP_ENC` uses:

```sh
export LUKS_KEYFILE_APP_ENC=/secure/per-device/app-enc.key
export LUKS_UUID_APP_ENC=01234567-89ab-cdef-0123-456789abcdef
./initrd-flash
```

`APP_ENC_b` would use `LUKS_KEYFILE_APP_ENC_B` and
`LUKS_UUID_APP_ENC_B`. The key file must be readable and non-empty. The UUID
must be a valid UUID and becomes the LUKS UUID. It should be unique for each
flashed unit; it is also the passphrase-derivation context in the example
below.

The key file is a line-oriented derived LUKS passphrase, not the disk-encryption
key itself. `make-sdcard` supplies it on standard input to `cryptsetup`, which
matches the normal boot-time `nvluks-srv-app | cryptsetup luksOpen` use.

### Generating EKB and passphrase files

Use the tooling and key material appropriate to the product security process.
For example, an Orin EKB containing the disk encryption key can be generated
with:

```sh
python3 tools/disk-encryption/gen_ekb.py -chip t234 \
    -oem_k1_key oem_k1.key \
    -in_sym_key2 sym2_t234.key \
    -in_auth_key auth_t234.key \
    -out eks.img
```

To produce a generic passphrase file for the UUID/context above:

```sh
python3 tools/disk-encryption/gen_luks_passphrase.py \
    -k sym2_t234.key -g -c 01234567-89ab-cdef-0123-456789abcdef \
    > /secure/per-device/app-enc.key
```

Generic mode (`-g`) is the primary example: the boot-time unlock configuration
must derive the same generic passphrase with the same context. ECID-specific
mode is also possible, but the host-side derivation and the target's
`nvluks-srv-app` invocation must use matching ECID-specific inputs.

The target initramfs must be configured separately to use `nvluks-srv-app` and
the same context to open the encrypted rootfs. That target runtime integration
is product-specific and is not configured by this layer.

### Image sizing

`make-sdcard` writes the existing image with `dd` or `bmaptool` directly to the
opened LUKS mapper; it does not resize or copy the filesystem. The rootfs image
must therefore be smaller than the encrypted partition's LUKS payload. Reserve
space for the LUKS2 header when choosing the image and partition sizes. Flashing
fails before the image write if it does not fit in the mapper.

The LUKS format parameters are LUKS2, `aes-xts-plain64`, and a 256-bit key.
Temporary host mapper names include the flashing process ID and are closed after
flashing; they do not determine the mapper name used on the Jetson at boot.

## Thor: manual post-build workflow

The automated workflow above does not apply to Thor's unified-flash path. The
following manual post-build approach remains available for layouts requiring
encrypted partitions on Thor.

1. Modify the partition XML to set `encrypted` to true on the corresponding
   partition, as described in the [NVIDIA Disk Encryption Documentation](https://docs.nvidia.com/jetson/archives/r36.4/DeveloperGuide/SD/Security/DiskEncryption.html).

   ```xml
   <partition name="data-partition" type="data" encrypted="true">
   ```

2. Choose an initramfs init script that uses `luks-srv-app` and disables it
   after opening the rootfs. The context must match the context used when the
   encrypted image was prepared.

   ```sh
   __l4t_enc_root_dm="l4t_enc_root"
   __l4t_enc_root_dm_dev="/dev/mapper/${__l4t_enc_root_dm}"
   eval nvluks-srv-app -g -c "<context>" | cryptsetup luksOpen /dev/nvme0n1p${current_rootfs} ${__l4t_enc_root_dm}
   ```

After the Yocto build, use a privileged host-side script to create a LUKS image,
open it, create its filesystem, and copy the original rootfs into it. The LUKS
image must be the same size as the partition configured in XML.

```sh
GEN_LUKS_PASS_CMD="tools/gen_luks_passphrase.py"
genpass_opt=""
genpass_opt+=" -k tools/ekb.key "
genpass_opt+=" -g "
genpass_opt+=" -c '${__rootfsuuid}' "
GEN_LUKS_PASS_CMD+=" ${genpass_opt}"

truncate --size ${__rootfs_size} ${__rootfs_name}
eval ${GEN_LUKS_PASS_CMD} | sudo cryptsetup \
       --type luks2 \
       -c aes-xts-plain64 \
       -s 256 \
       --uuid "${__rootfsuuid}" \
       luksFormat \
       ${__rootfs_name}
eval ${GEN_LUKS_PASS_CMD} | sudo cryptsetup luksOpen ${__rootfs_name} ${__l4t_enc}
sudo mkfs.ext4 /dev/mapper/${__l4t_enc}
sudo mount /dev/mapper/${__l4t_enc} ${__enc_rootfs_mountpoint}
sudo mount ${__original_rootfs} ${__rootfs_original_mountpoint}
sudo tar -cf - -C ${__rootfs_original_mountpoint} . | sudo tar -xpf - -C ${__enc_rootfs_mountpoint}
sleep 5
sudo umount ${__enc_rootfs_mountpoint}
sudo cryptsetup luksClose ${__l4t_enc}
sudo umount ${__rootfs_original_mountpoint}
```
