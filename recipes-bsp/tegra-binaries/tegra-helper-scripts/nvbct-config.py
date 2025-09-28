#! /usr/bin/env python3

import os
import sys
import argparse
import logging
import xml.etree.ElementTree as ET

def main():
    parser = argparse.ArgumentParser(
        description="""
Updates values in an NVIDIA BCT configuration XML file.
""")
    parser.add_argument('-v', '--verbose', help='verbose logging', action='store_true')
    parser.add_argument('filename', help='name of the XML file to modify', action='store')
    parser.add_argument('settings', help='setting(s) of the form path/to/element=value', nargs='*')

    args = parser.parse_args()
    logging.basicConfig(format='%(message)s', level=logging.INFO if args.verbose else logging.WARNING)
    tree = ET.parse(args.filename)
    root = tree.getroot()
    if root.tag != "bct_cfg":
        raise ValueError("{} root is '{}', expected 'bct_cfg'".format(args.filename, root.tag))

    changed = False
    for s in args.settings:
        path, value = [x.strip() for x in s.strip().split('=')]
        elem = root.find(path)
        if elem is None:
            logging.error("{}: path not found".format(path))
            return 1
        logging.info("Updating {} from '{}' to '{}'".format(path, elem.text, value))
        elem.text = value
        changed = True

    if changed:
        tree.write(args.filename, encoding="UTF-8", xml_declaration=True, short_empty_elements=False)

    return 0

if __name__ == '__main__':
    try:
        ret = main()
    except Exception:
        ret = 1
        import traceback
        traceback.print_exc()
    sys.exit(ret)
