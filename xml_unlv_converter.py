import os
import glob
import argparse
from xml.etree import ElementTree as ET
from xml.dom import minidom


def xml_converter(args):
    filenames = glob.glob(os.path.join(args.input, "*.xml"))

    for xml_file in filenames:
        tree = ET.parse(xml_file)
        root = tree.getroot()

        # create the file structure
        out_root = ET.Element("GroundTruth")
        out_root.attrib['InputFile'] = root.find(".//path").text
        out_tables = ET.SubElement(out_root, "Tables")

        for obj in root.findall(".//object"):
            if "table" in obj[0].text:
                out_table = ET.SubElement(out_tables, "Table")
                for elem in obj.findall(".//bndbox"):
                    out_table.attrib['x0'] = elem[0].text
                    out_table.attrib['x1'] = elem[2].text
                    out_table.attrib['y0'] = elem[1].text
                    out_table.attrib['y1'] = elem[3].text

        out_data = minidom.parseString(ET.tostring(out_root)).toprettyxml(indent="   ")
        out_file = open(os.path.join(args.output, xml_file.split("/")[-1]), "w")
        out_file.write('<?xml version="1.0" encoding="UTF-8" standalone="no"?>\n')
        out_file.write('\n'.join(out_data.split('\n')[1:]))


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "-i",
        "--input",
        action="store",
        help="path to containing input xml files.",
        required=True,
    )
    parser.add_argument(
        "-o",
        "--output",
        action="store",
        help="path to store converted xml files.",
        required=True,
    )
    args = parser.parse_args()

    xml_converter(args)
