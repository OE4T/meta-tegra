def qemu_run_binary(data, rootfs_path, binary):
    libdir = rootfs_path + data.getVar("libdir", False)
    base_libdir = rootfs_path + data.getVar("base_libdir", False)
    mesa_stubsdir = rootfs_path + data.getVar("libdir", False) + "/mesa"
    libpaths = [mesa_stubsdir, libdir, base_libdir]

    return qemu_wrapper_cmdline(data, rootfs_path, libpaths) + rootfs_path + binary
