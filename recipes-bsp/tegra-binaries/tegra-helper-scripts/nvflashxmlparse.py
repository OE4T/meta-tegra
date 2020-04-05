#! /usr/bin/env python3

import sys
import argparse
import uuid

uuids = {}

def generate_guid(guid):
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
    def __init__(self, element, sector_size):
        self.name = element.get('name')
        self.id = element.get('id')
        self.type = element.get('type')
        self.oem_sign = element.get('oemsign', 'false') == 'true'
        guid = element.find('unique_guid')
        self.partguid = "" if guid is None else validate_guid(guid.text.strip())
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
        else:
            s = element.find('size').text.strip()
            try:
                self.size = (int(s, 10) + sector_size-1) // sector_size
            except ValueError:
                self.size = s
        fname = element.find('filename')
        self.filename = "" if fname is None else fname.text.strip()

    def filltoend(self):
        return (self.alloc_attr & 0x800) == 0x800
    def is_partition_table(self):
        return self.type in ['GP1', 'GPT', 'protective_master_boot_record', 'primary_gpt', 'secondary_gpt']

class Device(object):
    def __init__(self, element):
        self.type = element.get('type')
        self.instance = element.get('instance')
        self.sector_size = int(element.get('sector_size', '512'), 0)
        self.num_sectors = int(element.get('num_sectors', '0'), 0)
        self.partitions = []
        for partnode in element.findall('./partition'):
            self.partitions.append(Partition(partnode, self.sector_size))

class PartitionLayout(object):
    def __init__(self, configfile):
        import xml.etree.ElementTree as ET
        tree = ET.parse(configfile)
        root = tree.getroot()
        if root.tag != 'partition_layout':
            raise ValueError("{} root is '{}', expected 'partition_layout'".format(configfile, root.tag))
        self.devices = {}
        self.devtypes = []
        self.device_count = 0
        for devnode in tree.findall('./device'):
            dev = Device(devnode)
            if dev.type == 'sdmmc':
                dev.type = 'sdmmc_boot' if self.device_count == 0 else 'sdmmc_user'
            if dev.type in self.devices:
                raise ValueError("{} contains multiple devices of same type".format(configfile))
            self.devices[dev.type] = dev
            self.device_count += 1
            self.devtypes.append(dev.type)

def main():
    parser = argparse.ArgumentParser(
        description="""
Extracts partition information from an NVIDIA flash.xml file
""")
    parser.add_argument('-t', '--type', help='device type to extract information for', action='store')
    parser.add_argument('-l', '--list-types', help='list the device types described in the file', action='store_true')
    parser.add_argument('filename', help='name of the XML file to parse', action='store')

    args = parser.parse_args()
    layout = PartitionLayout(args.filename)
    if args.list_types:
        print("Device types:\n{}".format('\n'.join(['    ' + t for t in layout.devtypes])))
        return 0
    if not args.type:
        if layout.device_count > 1:
            raise RuntimeError("Must specify --type for layouts with multiple devices")
        args.type = layout.devtypes[0]
    partitions = [part for part in layout.devices[args.type].partitions if not part.is_partition_table()]
    blksize = layout.devices[args.type].sector_size
    for n, part in enumerate(partitions):
        print("blksize={};partnumber={};partname=\"{}\";partsize={};"
              "partfile=\"{}\";partguid=\"{}\";partfilltoend={}".format(blksize,
                                                                        n+1,
                                                                        part.name,
                                                                        part.size,
                                                                        part.filename,
                                                                        part.partguid,
                                                                        1 if part.filltoend() else 0))

if __name__ == '__main__':
    try:
        ret = main()
    except Exception:
        ret = 1
        import traceback
        traceback.print_exc()
    sys.exit(ret)
