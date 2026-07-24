def get_hex_version(l4t_version):
    verparts = l4t_version.split('.')
    branch = int(verparts[0])
    branch_high = (branch >> 8) & 0xff
    branch_low = branch & 0xff
    major = int(verparts[1]) & 0xff
    minor = int(verparts[2]) & 0xff
    return "0x%02x%02x%02x%02x" % (branch_high, branch_low, major, minor)

def derive_config_name(d):
    if not bb.utils.to_boolean(d.getVar('TEGRA_MINIMAL_BOOT')):
        return 'general'
    if bb.utils.to_boolean(d.getVar('TEGRAFLASH_NO_INTERNAL_STORAGE') or '0') or d.getVar('TNSPEC_BOOTDEV').startswith('nvme'):
        return 'simple_pcie'
    return 'simple_emmc'

def default_boot_order(d):
    if not bb.utils.to_boolean(d.getVar('TEGRA_MINIMAL_BOOT')):
        return ''
    if bb.utils.to_boolean(d.getVar('TEGRAFLASH_NO_INTERNAL_STORAGE') or '0') or d.getVar('TNSPEC_BOOTDEV').startswith('nvme'):
        return 'BootOrderNvme.dtbo'
    if d.getVar('TNSPEC_BOOTDEV_DEFAULT') == "mmcblk1p1":
        return 'BootOrderSD.dtbo'
    return 'BootOrderEmmc.dtbo'
