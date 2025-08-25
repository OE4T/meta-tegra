
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

def concat_dtb_overlays(dtbfile, overlays, outfile, d):
    """
    Produce an output DTB file that has zero or more
    overlay DTBs concatenated to it, for processing by
    NVIDIA's OverlayManager UEFI driver.

    Source DTB and overlay files can reside either under
    ${EXTERNAL_KERNEL_DEVICETREE} or in ${DEPLOY_DIR_IMAGE}.

    dtbfile: Main DTB file
    overlays: space-separated list of zero or more DTBO files
    outfile: name of output file
    d: recipe context
    """
    import os
    import bb.utils


    infiles = locate_dtb_files([dtbfile] + overlays.split(), d)
    bb.note("Creating concatenated device tree: ", outfile)
    with open(outfile, "wb") as outf:
        for infile in infiles:
            # the overlay manager expects all DTBOs to start
            # on a 4K page boundary
            outf.write(bytearray(int((outf.tell() + 4095) / 4096) * 4096))
            bb.note("    Adding:  ", infile)
            with open(infile, "rb") as inf:
                outf.write(inf.read())
