Jetson secure boot support in L4T R35.2.1 implements a different chain of trust from what was present in the [L4T R32](https://github.com/OE4T/meta-tegra/wiki/Secure-Boot-Support) releases:

* The Trusty secure OS has been replaced by OP-TEE, which allows for dynamic loading of trusted applications (TAs) from the non-secure world. TAs must be signed, and the public key used for checking the signature is compiled into the OP-TEE OS.
* The cboot bootloader has been replaced by UEFI, which uses its own set of keys for validating signatures on binaries that it loads (Linux kernel, EFI applications, and EFI capsules).

**NOTE** NVIDIA made some changes to the UEFI bootloader in L4T R35.5.0 that require that an "authentication key" be programmed into the Encrypted Key Block on secured devices. If you are updating your secured device from an earlier R35.x release to R35.5.0, you *must* update the EKB on the device with the added key.  See [this developer forum thread](https://forums.developer.nvidia.com/t/284400/8) for more information.

# Getting started
Start by reading the [Secure Boot](https://docs.nvidia.com/jetson/archives/r36.4.3/DeveloperGuide/SD/Security/SecureBoot.html) section of the Jetson Linux Developer's Guide.

The sections below cover specifics of how secure boot and signing are implemented for OE/Yocto builds with meta-tegra.

# Bootloader signing

## Setting fuses for secure boot ##
Follow the instructions in the NVIDIA documentation for generating keys and burning secure boot fuses for your Jetson device. Be warned that burning the fuses is a **one-time** operation, so be extremely careful. You could render your Jetson **permanently unbootable** if something goes wrong during the fuse burning process.


## Build-time bootloader signing
If you have the bootloader signing and encryption key files available, you can add the following setting to your `local.conf` to create signed boot images and BUP packages:
```
TEGRA_SIGNING_ARGS = "-u /path/to/pkc-signing-key.pem -v /path/to/sbk.key --user_key /path/to/user.key"
```
These arguments parallel the ones used with the L4T `flash.sh` script for signing:
* The `-u` option takes the path name of the RSA private key for PKC signing.
* The `-v` option takes the path name of the SBK key used for encrypting the binaries loaded at boot time.
* The `--user_key` option takes the path name of the encryption key you create for use with the [NVIDIA sample OP-TEE TAs](https://docs.nvidia.com/jetson/archives/r36.4.3/DeveloperGuide/SD/Security/OpTee.html#sample-applications).

Note that with R35.2.1, the `--user_key` encryption key is used only for the XUSB firmware.  Starting with R35.3.1, the user encryption key is not used for *any* of the boot firmware.

Build-time bootloader signing will be performed on the boot-related files in the `tegraflash` package for flashing, as well as the entries in any bootloader update payloads (BUPs).

## Post-build signing
You can elect to perform bootloader signing outside of the build process by adding the `-u`, `-v`, and `--user_key` options when running the `doflash.sh` or `initrd-flash` script during flashing of your `tegraflash` package.  For BUP generation, add those options when running the `generate_bup_payload.sh` script to have the bootloader components signed. 

# UEFI Secure Boot

To enable UEFI secure boot support, start by generating the PK, KEK, and DB keys and related configuration files, as described in the [UEFI Secure Boot](https://docs.nvidia.com/jetson/archives/r36.4.3/DeveloperGuide/SD/Security/SecureBoot.html#uefi-secure-boot) section of the Jetson Linux documentation.

It should be noted that UEFI boot is not compatible with the legacy secure boot supported on Tegra devices.

## Build-time UEFI signing

During the build, signing of the EFI launcher app, the kernel, and device tree files is performed automatically when the following settings are present in your build configuration:
```
TEGRA_UEFI_DB_KEY = "/path/to/db.key"
TEGRA_UEFI_DB_CERT = "/path/to/db.crt"
```

Both settings must be present, and must point to one of the DB keys you generated (you do not need the PK or KEK keys).

## Post-build UEFI signing

Post-build UEFI signing is not currently supported.

## Enrolling UEFI keys at build time
To enable UEFI secure boot, the PK, KEK, and DB keys you generated must be "enrolled" at boot time. On Jetson platforms, this done by adding the needed key enrollment variable settings to the bootloader's device tree via the `UefiDefaultSecurityKeys.dts` file you generated when creating the keys and configuration files. For meta-tegra builds, you can supply this file by adding a bbappend for the `tegra-uefi-keys-dtb.bb` recipe in one of your own metadata layers, substituting variables MY_LAYER with the path to your layer and MY_UEFI_KEYS_DIR with the path to your uefi_keys directory setup after following instructions linked above
```
export MY_LAYER=tegra-demo-distro/layers/meta-tegrademo
export MY_UEFI_KEYS_DIR=~/uefi_keys/
mkdir -p ${MY_LAYER}/recipes-bsp/uefi
cat > ${MY_LAYER}/recipes-bsp/uefi/tegra-uefi-keys-dtb.bbappend <<'EOF'
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
EOF
mkdir -p ${MY_LAYER}/recipes-bsp/uefi/files
cp ${MY_UEFI_KEYS_DIR}/UefiDefaultSecurityKeys.dts ${MY_LAYER}/recipes-bsp/uefi/files/
echo "Copy below is optional, only needed if you plan to update your keys with a capsule update"
cp ${MY_UEFI_KEYS_DIR}/UefiUpdateSecurityKeys.dts ${MY_LAYER}/recipes-bsp/uefi/files/
```

## Enrolling UEFI keys at runtime
The Jetson Linux documentation describes the process for enrolling UEFI keys and enabling UEFI secure boot at runtime. You will need to add some packages to your image build to make the necessary commands available.  As of this writing, runtime enrollment has not been tested.

# OP-TEE Trusted Application signing
OP-TEE provides a mechanism for loading TAs from the "Rich Execution Environment" (REE, another term for the normal, non-secure OS), which must be signed with a key that is known the OP-TEE OS.  Read the [OP-TEE documentation on TAs](https://optee.readthedocs.io/en/latest/building/trusted_applications.html#) for more information.

By default, a development/test key from the upstream OP-TEE source is compiled in; this configuration should **not** be used in any production device, since the key is publicly available.  You should generate a suitable RSA keypair as described in the OP-TEE documentation.  For build-time signing, add a bbappend for the `optee-os` recipe in one of your layers.  For build-time signing, your bbappend should resemble the following:
```
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += "file://optee-signing-key.pem"
EXTRA_OEMAKE += "TA_SIGN_KEY=${WORKDIR}/optee-signing-key.pem"
```
Post-build signing of TAs is more difficult, since external TAs are generally packaged and installed into the root filesystem as part of the build. For that approach, though, you would include the public key file in the `optee-os` bbappend, and set `TA_PUBLIC_KEY` instead of `TA_SIGN_KEY`.  The OP-TEE makefiles will sign TAs with the a dummy private key, but the public key you specify will be compiled into the secure OS.  You will have to figure out how to re-sign the TAs with your actual private key before they get used.

# Using the NVIDIA built-in sample TAs
To make use of the encryption/decryption functions NVIDIA provides by default with their OP-TEE implementation, you will need to supply an "Encrypted Keyblob" (EKB) that corresponds to the KEK/K2 fuses you have burned on your Jetson device.  Instructions for generating an EKB are in [this section](https://docs.nvidia.com/jetson/archives/r36.4.3/DeveloperGuide/SD/Security/OpTee.html#tool-for-ekb-generation) of the Jetson Linux documentation.  See the note at the top of this page for information about changes in L4T R35.5.0 that require the re-generation of the EKB.

The `tegra-bootfiles` recipe installs the default EKB from the L4T kit.  Add a bbappend for that recipe to replace the default with the custom EKB for your device.

## Generating a Custom EKB
Before replacing the default EKB in your Yocto build, you must generate a custom one that matches OemK1 fuse burned on your Jetson device. To do this, you need the `gen_ekb.py` script from the NVIDIA OP-TEE samples code base (for the `hwkey-agent` sample). You can find that script either in the L4T public sources tarball, or on [NVIDIA's git server](https://nv-tegra.nvidia.com/r/plugins/gitiles/tegra/optee-src/nv-optee) (making sure you choose the branch for the L4T version you are targeting).

Example:
```
python3 gen_ekb.py -chip t234 \
    -oem_k1_key oem_k1.key \
    -in_sym_key2 sym2_t234.key \
    -in_auth_key auth_t234.key \
    -out eks_t234.img
```
where
* `oem_k1.key` is the OEM_K1 key stored in the OEM_K1 fuse.
* `sym2_t234.key` is the disk encryption key.
* `auth_t234.key` is the UEFI variable authentication key
* `eks_t234.img` is the generated EKB image to be flashed to the EKS partition of the device

Kernel encryption is not currently supported in meta-tegra, so do *not* provide the UEFI payload encryption key (using `-in_sym_key`).

