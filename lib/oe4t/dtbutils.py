
def locate_dtb_files(dtbnames, d):
    """
    Locate the full path of dtb files given
    a list of names and the relevant bitbake
    variables used for dtb locations.
    dtbnames: Names of dtb files to search for
    return: a list of full paths to dtb files
    matching dtbnames
    """

    def find_file_under(fname, rootdir):
        for dirname, _, f in os.walk(rootdir):
            if fname in f:
                return os.path.join(dirname, fname)
        return None

    result = []
    extern_root = d.getVar('EXTERNAL_KERNEL_DEVICETREE')
    imgdeploydir = d.getVar('DEPLOY_DIR_IMAGE')
    for dtb in dtbnames:
        if os.path.isabs(dtb):
            result.append(dtb)
            continue
        if extern_root:
            dtbpath = find_file_under(dtb, extern_root)
            if dtbpath:
                result.append(dtbpath)
                continue
        result.append(os.path.join(imgdeploydir, dtb))
    return result

def copy_dtb_files(overlays, outdir, d):
    """
    Copy the list of overlays into outdir
    overlays: List of overlays to copy
    outdir: Output directory to copy into
    """
    import shutil
    infiles = locate_dtb_files(overlays.split(), d)
    for infile in infiles:
        shutil.copy(infile, outdir)

