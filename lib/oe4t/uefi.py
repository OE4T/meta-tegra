def get_hex_version(l4t_version):
    verparts = l4t_version.split('.')
    branch = int(verparts[0])
    branch_high = (branch >> 8) & 0xff
    branch_low = branch & 0xff
    major = int(verparts[1]) & 0xff
    minor = int(verparts[2]) & 0xff
    return "0x%02x%02x%02x%02x" % (branch_high, branch_low, major, minor)
