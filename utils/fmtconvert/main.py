#!/usr/bin/env python3
import argparse
import itertools
import json
import logging
import os
from pathlib import Path
from typing import Dict, List

import predefined
from util import init_logging

logger = logging.getLogger(__name__)


def main():
    args = handle_args()
    json_data = generate_json_data(args.input)
    out_file: Path = Path(args.out)
    with open(out_file, 'w') as json_file:
        json.dump(json_data, json_file, indent=2)


def assert_predefined():
    assert len(predefined.instructions) == 5
    assert len(predefined.output_type_vi) == 5


def generate_json_data(directory_path):
    assert_predefined()
    json_data: List = []
    for root, dirs, files in os.walk(directory_path):
        # for each iteration
        for subdir in dirs:
            input_string = ""
            # in each iteration, there are multiple files to be concat together
            for filename in os.listdir(os.path.join(root, subdir)):
                if filename.endswith(".java"):
                    file_path = os.path.join(root, subdir, filename)
                    file_content = read_file_content(file_path)
                    input_string += file_content + "\n"
            for (instruct, output) in itertools.product(predefined.instructions,
                                                        predefined.output_type_vi):
                json_entry: Dict = {
                    "input": input_string,
                    "output": output,
                    "instruction": instruct,
                }
                json_data.append(json_entry)
    return json_data


def read_file_content(file_path):
    with open(file_path, 'r') as file:
        return file.read()


def handle_args():
    parser = argparse.ArgumentParser(description='Convert generated source files to JSON')
    parser.add_argument('-l', metavar='LOG_LEVEL', type=str)
    parser.add_argument('--input', metavar='INPUT_DIR', required=True, type=str,
                        help='Input directory of source files')
    parser.add_argument('--out', metavar="OUT_FILE")

    args = parser.parse_args()
    init_logging(args.l)
    logger.debug(args)
    return args


if __name__ == "__main__":
    main()
