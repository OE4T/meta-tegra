#! /usr/bin/env python3

import os
import sys
import argparse
import uuid
import logging
import xml.etree.ElementTree as ET

uuids = {}
ignore_partition_ids = False
boot_devices = ["spi", "sdmmc_boot"]
kernels_and_dtbs = ["kernel", "kernel_b", "kernel-dtb", "kernel-dtb_b",
                    "A_kernel", "B_kernel", "A_kernel-dtb", "B_kernel-dtb",
                    "recovery", "RECNAME", "recovery-dtb", "RECDTB-NAME"]
kernel_partname_map = {
    "kernel": "A_kernel",
    "kernel-dtb": "A_kernel-dtb",
    "kernel_b" : "B_kernel",
    "kernel-dtb_b" : "B_kernel-dtb"
}
primary_gpt_types = ['GP1', 'protective_master_boot_record', 'primary_gpt']
secondary_gpt_types = ['GPT', 'secondary_gpt']
all_gpt_types = primary_gpt_types + secondary_gpt_types

def generate_guid(guid):
    global uuids
    try:
        return uuids[guid]
    except KeyError:
        uuids[guid] = uuid.uuid4()
    return uuids[guid]

def validate_guid(guid):
    expected_lengths = [8, 4, 4, 4, 12]
    if len(guid) != 36:
        return generate_guid(guid)
    guidparts = guid.split('-')
    if len(guidparts) != 5:
        return generate_guid(guid)
    for i in range(0, 5):
        if len(guidparts[i]) != expected_lengths[i]:
            return generate_guid(guid)
        try:
            partval = int(guidparts[i], 16)
        except ValueError:
            return generate_guid(guid)
    return guid

class Partition(object):
    def __init__(self, element, sector_size, curpos, partnum, bootdev, ignore_id):
        self.name = element.get('name')
        self.type = element.get('type')
        default_id = None if self.is_partition_table() else partnum
        self.id = default_id if ignore_id else element.get('id', default_id)
        self.oem_sign = element.get('oemsign', 'false') == 'true'
        guid = element.find('unique_guid')
        self.partguid = "" if guid is None else validate_guid(guid.text.strip())

        parttype = element.find('partition_type_guid')
        self.parttype = "" if parttype is None else parttype.text.strip()

        fstype = element.find('filesystem_type')
        self.fstype = "" if fstype is None else fstype.text.strip()

        alignment = element.find('align_boundary')
        if alignment is not None:
            alignval = int(alignment.text.strip(), base=0) # in bytes
            curposbytes = alignval * ((curpos * sector_size + alignval-1) // alignval)
            curpos = curposbytes // sector_size
        startloc = element.find('start_location')
        if startloc is None:
            self.start_location = curpos
        else:
            self.start_location = (int(startloc.text.strip(), base=0) + sector_size-1) // sector_size
            if self.start_location < curpos:
                raise RuntimeError("partition {} start location {} overlaps previous partition".format(self.name, self.start_location))

        aa = element.find('allocation_attribute')
        if aa is None:
            self.alloc_attr = 0
        else:
            aastr = aa.text.strip()
            if aastr.startswith("0x"):
                aastr = aastr[2:]
            self.alloc_attr = int(aastr, 16)

        if element.find('size') is None:
            self.size = 0
        elif self.name == "GP1":
            # NV flash tools (for t210) ignore this size setting
            # and just use the standard size
            self.size = 34
        else:
            s = element.find('size').text.strip()
            if s.lower().startswith('0xffffffff'):
                self.size = -1
            else:
                self.size = int(s, 0)
            if self.size > 0:
                self.size = (self.size + sector_size-1) // sector_size

        fname = element.find('filename')
        self.filename = "" if fname is None or fname.text is None else fname.text.strip()
        logging.info("Partition {}: id={}, type={}, start={}, size={}, parttype={}, fstype={}".format(self.name, self.id, self.type,
                                                                                          self.start_location, self.size,
                                                                                          self.parttype, self.fstype)

    def filltoend(self):
        return (self.alloc_attr & 0x800) == 0x800

    def is_partition_table(self, primary_only=False):
        if self.type in primary_gpt_types:
            return True
        return not primary_only and self.type in secondary_gpt_types

class Device(object):
    def __init__(self, element, devcount):
        self.type = element.get('type')
        self.instance = element.get('instance')
        self.sector_size = int(element.get('sector_size', '512'), 0)
        self.num_sectors = int(element.get('num_sectors', '0'), 0)
        self.partitions = []
        self.is_boot_device = self.type in ['sdmmc_boot', 'spi'] or (self.type == 'sdmmc' and devcount == 0)
        self.ignore_ids = ignore_partition_ids or self.is_boot_device
        logging.info("Device {}: instance={}, sector_size={}, num_sectors={}, boot_device={}".format(self.type, self.instance,
                                                                                                     self.sector_size, self.num_sectors,
                                                                                                     self.is_boot_device))
        self.parttable_size = 0
        nextpart = 1
        curpos = 0
        for partnode in element.findall('./partition'):
            part = Partition(partnode, self.sector_size, curpos, nextpart, self.is_boot_device, self.ignore_ids)
            self.partitions.append(part)
            if part.is_partition_table(primary_only=True):
                self.parttable_size += part.size
            if not part.is_partition_table():
                nextpart = int(part.id) + 1
            if part.type != 'secondary_gpt' and not part.filltoend():
                curpos = part.start_location + part.size


class PartitionLayout(object):
    def __init__(self, configfile):
        tree = ET.parse(configfile)
        root = tree.getroot()
        if root.tag != 'partition_layout':
            raise ValueError("{} root is '{}', expected 'partition_layout'".format(configfile, root.tag))
        self.devices = {}
        self.devtypes = []
        self.device_count = 0
        for devnode in tree.findall('./device'):
            dev = Device(devnode, self.device_count)
            if dev.type == 'sdmmc':
                dev.type = 'sdmmc_boot' if self.device_count == 0 else 'sdmmc_user'
            if dev.type in self.devices:
                raise ValueError("{} contains multiple devices of same type".format(configfile))
            self.devices[dev.type] = dev
            self.device_count += 1
            self.devtypes.append(dev.type)


def extract_layout(infile, devtype, outf, new_devtype=None, sector_count=0):
    tree = ET.parse(infile)
    root = tree.getroot()
    if devtype not in ["boot", "rootfs"]:
        actual_devtype = devtype
    for dev in root.findall('device'):
        if devtype == "boot":
            if dev.get('type') not in boot_devices:
                root.remove(dev)
            else:
                actual_devtype = dev.get('type')
        elif devtype == "rootfs":
            if dev.get('type') in boot_devices:
                root.remove(dev)
            else:
                actual_devtype = dev.get('type')
        elif dev.get('type') != devtype:
            root.remove(dev)
    logging.info("For devtype {}, actual devtype is {}".format(devtype, actual_devtype))
    devs = root.findall('device')
    if len(devs) != 1:
        raise RuntimeError("{} unexpectedly contains multiple devices after extraction".format(infile))
    dev = devs[0]
    if new_devtype:
        if dev.get('type') != actual_devtype:
            raise RuntimeError("Could not convert device type {} to {}: old type not found".format(actual_devtype, new_devtype))
        dev.set('type', new_devtype)
        dev.set('instance', '3' if new_devtype == 'sdmmc_user' else '0')
    if sector_count != 0:
        dev.set('num_sectors', str(sector_count))
    tree.write(outf, encoding='unicode', xml_declaration=True)
    outf.write("\n")


def split_layout(infile, mmcf, sdcardf, new_devtype=None, sector_count=0):
    if new_devtype is None:
        new_devtype = "sdcard"
    sdcard_parts = ['APP', 'APP_b', 'UDA', 'RECROOTFS', 'esp']
    if new_devtype == "sdcard":
        sdcard_parts += kernels_and_dtbs
    mmctree = ET.parse(infile)
    sdcardtree = ET.parse(infile)
    root = mmctree.getroot()
    for dev in root.findall('device'):
        if dev.get('type') == 'sdmmc_user':
            for partnode in dev.findall('partition'):
                if partnode.get('type') in all_gpt_types:
                    continue
                partname = partnode.get('name')
                if partname in sdcard_parts:
                    dev.remove(partnode)
                    logging.info("For {}, removed {}".format(dev.get('type'), partname))
    root = sdcardtree.getroot()
    for dev in root.findall('device'):
        if dev.get('type') == 'sdmmc_user':
            if new_devtype:
                dev.set('type', new_devtype)
                dev.set('instance', '3' if new_devtype == 'sdmmc_user' else '0')
            if sector_count != 0:
                dev.set('num_sectors', str(sector_count))
            for partnode in dev.findall('partition'):
                if partnode.get('type') in all_gpt_types:
                    continue
                partname = partnode.get('name')
                if partname not in sdcard_parts:
                    dev.remove(partnode)
                    logging.info("For {}, removed {}".format(new_devtype or "SDcard", partname))
        else:
            root.remove(dev)
    mmctree.write(mmcf, encoding='unicode', xml_declaration=True)
    mmcf.write("\n")
    sdcardtree.write(sdcardf, encoding='unicode', xml_declaration=True)
    sdcardf.write("\n")


def replace_filename(part, maptree):
    for dev in maptree.findall('device'):
        for mapnode in dev.findall('partition'):
            if part.get('name') != mapnode.get('name'):
                continue
            filename = part.find('filename')
            if filename is not None:
                logging.info("Removed old filename for {}: {}".format(part.get('name'), filename.text))
                part.remove(filename)
            filename = mapnode.find('filename')
            if filename is None:
                logging.info("No replacement filename for partition {}".format(part.get('name')))
                return
            # Keep <description> element last, if there is one
            if len(part) > 0 and part[len(part)-1].tag == 'description':
                fnelem = ET.Element('filename')
                fnelem.text = filename.text
                fnelem.tail = filename.tail
                part.insert(len(part)-1, fnelem)
                logging.info("New filename element inserted for {}: {}".format(part.get('name'), fnelem.text))
            else:
                fnelem = ET.SubElement(part, 'filename')
                fnelem.text = filename.text
                fnelem.tail = filename.tail
                logging.info("New filename element appended for {}: {}".format(part.get('name'), part[len(part)-1].text))
            return
    logging.info("No rewrite applied for {}".format(part.get('name')))

def rewrite_layout(infile, mapfiles, outf):
    intree = ET.parse(infile)
    for mapfile in mapfiles:
        maptree = ET.parse(mapfile)
        root = intree.getroot()
        for dev in root.findall('device'):
            for part in dev.findall('partition'):
                replace_filename(part, maptree)
    intree.write(outf, encoding='unicode', xml_declaration=True)
    outf.write("\n")

def update_part_size(part, tmpltree):
    partname = part.get('name')
    oldsize = part.find('size')
    for dev in tmpltree.findall('device'):
        for tmplnode in dev.findall('partition'):
            if partname == tmplnode.get('name'):
                newsize = tmplnode.find('size')
                logging.info("Updating size of partition {} from {} to {}".format(partname,
                                                                                  oldsize.text.strip(),
                                                                                  newsize.text.strip()))
                oldsize.text = " " + newsize.text.strip() + " "
                return

def update_sizes(infile, template, parttypes, outf):
    intree = ET.parse(infile)
    tmpltree = ET.parse(template)
    root = intree.getroot()
    for dev in root.findall('device'):
        for part in dev.findall('partition'):
            if part.get('type') in parttypes:
                update_part_size(part, tmpltree)
    intree.write(outf, encoding='unicode', xml_declaration=True)
    outf.write("\n")

def remove_from_layout(infile, to_remove, outf):
    intree = ET.parse(infile)
    root = intree.getroot()
    if not to_remove:
        to_remove = kernels_and_dtbs
    for dev in root.findall('device'):
        for part in dev.findall('partition'):
            if part.get('name') in to_remove:
                logging.info("Removing {} from {}".format(part.get('name'), dev.get('type')))
                dev.remove(part)
    intree.write(outf, encoding='unicode', xml_declaration=True)
    outf.write("\n")

def rename_kernel_partitions(infile, outf):
    intree = ET.parse(infile)
    root = intree.getroot()
    for dev in root.findall('device'):
        for part in dev.findall('partition'):
            oldname = part.get('name')
            if oldname in kernel_partname_map:
                logging.info("Renaming {} to {}".format(oldname, kernel_partname_map[oldname]))
                part.set('name', kernel_partname_map[oldname])
    intree.write(outf, encoding='unicode', xml_declaration=True)
    outf.write("\n")

def size_to_sectors(sizespec):
    suffix = sizespec[-1:]
    if suffix in ['G', 'K', 'M']:
        sizespec = sizespec[:-1]
    try:
        size = int(sizespec)
    except ValueError:
        raise RuntimeError("SDcard size must be integral number of sectors, or suffixed with G, K, or M")

    if suffix == 'K':
        size *= 1000
    elif suffix == 'M':
        size *= 1000 * 1000
    elif suffix == 'G':
        size *= 1000 * 1000 * 1000
    else:
        return size

    # For suffixed specs, convert bytes to sectors,
    # reserving 1% for overhead
    return int(int(size * 99 / 100 + 511) / 512)

def get_filename_for_partition(infile, partname, outf) -> int:
    intree = ET.parse(infile)
    root = intree.getroot()
    for dev in root.findall('device'):
        for part in dev.findall('partition'):
            if part.get('name') == partname:
                filename = part.find('filename')
                if filename is not None:
                    print(filename.text.strip(), file=outf)
                else:
                    print("", file=outf)
                return 0
    logging.error("Partition not found: {}".format(partname))
    return 1

def main():
    global ignore_partition_ids
    ignore_from_env = os.getenv("NVFLASHXMLPARSE_IGNORE_PARTITION_IDS")
    if ignore_from_env is not None:
        ignore_from_env = ignore_from_env.strip().upper()
        ignore_partition_ids = ignore_from_env != "" and ignore_from_env in ['Y', 'YES', 'T', 'TRUE', '1']
    parser = argparse.ArgumentParser(
        description="""
Extracts/manipulates partition information in an NVIDIA flash layout XML file
""")
    parser.add_argument('-t', '--type', help='device type to extract information for, must either match a <device> tag or the generic "boot" or "rootfs" names', action='store')
    cmdgroup = parser.add_mutually_exclusive_group()
    cmdgroup.add_argument('-g', '--get-filename', help='extract the filename element for a partition', action='store')
    cmdgroup.add_argument('-l', '--list-types', help='list the device types described in the file', action='store_true')
    cmdgroup.add_argument('-b', '--boot-device', help='print the device type holding boot partitions', action='store_true')
    cmdgroup.add_argument('-e', '--extract', help='generate a new XML file extracting just the specified device type', action='store_true')
    cmdgroup.add_argument('--rewrite-contents-from', help='rewrite the <filename> entries in the output XML with the entries in the specified layout(s)', action='store')
    cmdgroup.add_argument('--update-parttype-sizes-from', help='layout:comma-separated list of parttypes for updating sizes in output XML', action='store')
    cmdgroup.add_argument('-s', '--split', help='XML output file for MMC-SDcard/external split on Jetson AGX Xavier', action='store')
    cmdgroup.add_argument('--remove', help='Remove partitions from the XML layout', action='store_true')
    cmdgroup.add_argument('--switch-to-prefixed-kernel-partitions', help='Rename kernel partitions from kernel/kernel_b to A_kernel/B_kernel', action='store_true')
    parser.add_argument('--change-device-type', help='(for use with --split or --extract) change the <device> tag type attribute to the specifed value', action='store')
    parser.add_argument('--partitions-to-remove', help='(for use with --remove) list of partitions to remove', action='store')
    parser.add_argument('-S', '--sdcard-size', '--storage-size', help='storage size for use with --split or --extract', action='store', default="0")
    parser.add_argument('-o', '--output', help='file to write output to', action='store')
    parser.add_argument('-v', '--verbose', help='verbose logging', action='store_true')
    parser.add_argument('filename', help='name of the XML file to parse', action='store')

    args = parser.parse_args()
    logging.basicConfig(format='%(message)s', level=logging.INFO if args.verbose else logging.WARNING)

    if args.get_filename:
        if args.output:
            outf = open(args.output, "w")
        else:
            outf = sys.stdout
        return get_filename_for_partition(args.filename, args.get_filename, outf)

    if args.split or args.rewrite_contents_from or args.extract or args.remove or args.update_parttype_sizes_from or args.switch_to_prefixed_kernel_partitions:
        if args.output:
            outf = open(args.output, "w")
        else:
            outf = sys.stdout
        if args.split:
            sdcardf = open(args.split, "w")
            split_layout(args.filename, outf, sdcardf, args.change_device_type, size_to_sectors(args.sdcard_size))
            return 0
        if args.rewrite_contents_from:
            rewrite_layout(args.filename, args.rewrite_contents_from.split(','), outf)
            return 0
        if args.update_parttype_sizes_from:
            template_layout, parttypenames = args.update_parttype_sizes_from.split(':')
            update_sizes(args.filename, template_layout, parttypenames.split(','), outf)
            return 0
        if args.extract:
            extract_layout(args.filename, args.type, outf, args.change_device_type, size_to_sectors(args.sdcard_size))
            return 0
        if args.remove:
            remove_from_layout(args.filename, args.partitions_to_remove, outf)
            return 0
        if args.switch_to_prefixed_kernel_partitions:
            rename_kernel_partitions(args.filename, outf)
            return 0
        print("Internal error dispatching to XML transformation function", file=sys.stderr)
        return 1

    layout = PartitionLayout(args.filename)
    if args.list_types:
        print("Device types:\n{}".format('\n'.join(['    ' + t for t in layout.devtypes])))
        return 0
    if args.boot_device:
        for devtype in layout.devtypes:
            if devtype in boot_devices:
                print("{}".format(devtype))
                return 0
        print("No boot device found in {}".format(args.filename), file=sys.stderr)
        return 1

    if not args.type:
        if layout.device_count > 1:
            raise RuntimeError("Must specify --type for layouts with multiple devices")
        args.type = layout.devtypes[0]
    else:
        # Support 'boot' as an alias for one of the boot device types ('spi', 'sdmmc_boot')
        # Support 'rootfs' as an alias for a device the is not one of boot device types
        if args.type in ['boot', 'rootfs']:
            if layout.device_count > 2:
                raise RuntimeError("Generic device types not supported for layouts with 3+ devices")
            devs = {}
            if layout.devtypes[0] in boot_devices:
                devs['boot'] = layout.devtypes[0]
                devs['rootfs'] = None if layout.device_count < 2 else layout.devtypes[1]
            else:
                devs['rootfs'] = layout.devtypes[0]
                devs['boot'] = None if layout.device_count < 2 or layout.devtypes[1] not in boot_devices else layout.devtypes[1]
            args.type = devs[args.type]
        if args.type is None or args.type not in layout.devices:
            logging.info("Requested device type no present, nothing to do")
            return 0

    if args.output:
        outf = open(args.output, "w")
    else:
        outf = sys.stdout

    partitions = [part for part in layout.devices[args.type].partitions if not part.is_partition_table()]
    blksize = layout.devices[args.type].sector_size
    for n, part in enumerate(partitions):
        print("blksize={};partnumber={};partname=\"{}\";start_location={};partsize={};"
              "partfile=\"{}\";partguid=\"{}\";parttype=\"{}\";fstype=\"{}\";partfilltoend={}".format(blksize,
                                                                                        part.id,
                                                                                        part.name,
                                                                                        part.start_location,
                                                                                        part.size,
                                                                                        part.filename,
                                                                                        part.partguid,
                                                                                        part.parttype,
                                                                                        part.fstype,
                                                                                        1 if part.filltoend() else 0),
              file=outf)
    outf.close()

if __name__ == '__main__':
    try:
        ret = main()
    except Exception:
        ret = 1
        import traceback
        traceback.print_exc()
    sys.exit(ret)
