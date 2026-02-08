This page describes one mechanism for enabling disk encryption on meta-tegra, using the notes from Islam Hussein in [this thread](https://matrix.to/#/!qJMWWUfzttaurEBEqL:gitter.im/$nAwSFU_-nveUAnJ_ZwF7ftRX19UF0yRoHyvvkm_WPh8?via=gitter.im&via=matrix.org&via=3dvisionlabs.com) on matrix.

The encryption happens as a post-process initiated manually after the build.

# Yocto changes
1. Modify your partition xml to set 'encrypted' to true on the corresponding partition, as described in the [NVIDIA Disk Encryption Documentation](https://docs.nvidia.com/jetson/archives/r36.4/DeveloperGuide/SD/Security/DiskEncryption.html).
```
<partition name="data-partition" type="data" encrypted="true">
```
2. Choose a different init script to be used in initramfs which uses `luks-srv-app` and disable it totally after that to prevent further use. See code snippet below.  For the "context", refer to the build changes section below.
```
__l4t_enc_root_dm="l4t_enc_root";
__l4t_enc_root_dm_dev="/dev/mapper/${__l4t_enc_root_dm}"
eval nvluks-srv-app -g -c "<context>" | cryptsetup luksOpen /dev/nvme0n1p${current_rootfs} ${__l4t_enc_root_dm}
```

# Build changes
Add a bash script to be called manually after finishing yocto build. The script will go to the path of the build, extract it in a temp directory. mount the rootfs and open it. Then it will make a luks-storage (And that’s why I couldn’t build it inside yocto) The problem is that when you want to open the luks using crypto you have to access device mapper which requires privileged access which yocto doesn’t have

* Store the size of rootfs which is written in xml it have to be the same and then create luks drive with the same size. 
* To generate the password you’ll need to run `gen_ekb.py`
* You’ll have to to write down dummy uuid which is the context used in the code snippet above. (context will be used in two places generating pass to encrypt rootfs and generating the pass access it.)
* One way is to use a generic password which doesn’t need ecid. So the same key will be used for all of my devices.

```
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
sudo mount  ${__original_rootfs} ${__rootfs_original_mountpoint}
sudo tar -cf - -C ${__rootfs_original_mountpoint} . | sudo tar -xpf - -C ${__enc_rootfs_mountpoint}
sleep 5
sudo umount ${__enc_rootfs_mountpoint}
sudo cryptsetup luksClose ${__l4t_enc}
sudo umount ${__rootfs_original_mountpoint}
```

