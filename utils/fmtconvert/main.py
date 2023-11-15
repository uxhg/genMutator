#!/usr/bin/env python3
import argparse
import itertools
import json
import logging
import os
import random
from pathlib import Path
from typing import Dict, List

import predefined
from util import init_logging

logger = logging.getLogger(__name__)


def main():
    args = handle_args()
    json_data = generate_json_data(args.input, args.p, int(args.type), args.r)
    out_file: Path = Path(args.out)
    with open(out_file, 'w') as json_file:
        json.dump(json_data, json_file, indent=2)


def assert_predefined():
    assert len(predefined.instructions) == 5
    assert len(predefined.output_type_vi) == 5
    assert len(predefined.output_type_i) == 5
    assert len(predefined.output_type_iii) == 5


def choose_output(x: int):
    if x == 1:
        output_list = predefined.output_type_i
    elif x == 3:
        output_list = predefined.output_type_iii
    elif x == 6:
        output_list = predefined.output_type_vi
    else:
        raise ValueError("The issue type must be 1, 3, or 6 to set outputs")
    return output_list


def generate_json_data(directory_path, concat_sub_dir: bool, issue_type: int,
                       rand_pick_one: bool = False):
    assert_predefined()
    json_data: List = []
    output_lists = choose_output(issue_type)
    for root, dirs, files in os.walk(directory_path):
        # for each iteration
        for subdir in dirs:
            if concat_sub_dir:
                # in each iteration, there are multiple files to be concat together
                input_string = concat_multiple_files_in_dir_to_str(root, subdir)
                if rand_pick_one:
                    json_entry: Dict = {
                        "input": input_string,
                        "output": random.choice(output_lists),
                        "instruction": random.choice(predefined.instructions)
                    }
                    json_data.append(json_entry)
                else:
                    for (instruct, output) in itertools.product(predefined.instructions,
                                                                output_lists):
                        json_entry: Dict = {
                            "input": input_string,
                            "output": output,
                            "instruction": instruct,
                        }
                        json_data.append(json_entry)
            else:
                for filename in os.listdir(os.path.join(root, subdir)):
                    if filename.endswith(".java"):
                        file_path = os.path.join(root, subdir, filename)
                        file_content = read_file_content(file_path)
                        if rand_pick_one:
                            json_entry: Dict = {
                                "input": file_content,
                                "output": random.choice(output_lists),
                                "instruction": random.choice(predefined.instructions)
                            }
                            json_data.append(json_entry)
                        else:
                            for (instruct, output) in itertools.product(predefined.instructions,
                                                                        output_lists):
                                json_entry: Dict = {
                                    "input": file_content,
                                    "output": output,
                                    "instruction": instruct
                                }
                                json_data.append(json_entry)
    return json_data


def concat_multiple_files_in_dir_to_str(root, subdir):
    input_string = ""
    for filename in os.listdir(os.path.join(root, subdir)):
        if filename.endswith(".java"):
            file_path = os.path.join(root, subdir, filename)
            file_content = read_file_content(file_path)
            input_string += file_content + "\n"
    return input_string


def read_file_content(file_path):
    with open(file_path, 'r') as file:
        return file.read()


def handle_args():
    parser = argparse.ArgumentParser(description='Convert generated source files to JSON')
    parser.add_argument('-l', metavar='LOG_LEVEL', type=str)
    parser.add_argument('-t', "--type", metavar='Type_Num', type=int, required=True,
                        help='Type number for specifying outputs')
    parser.add_argument('-p', action="store_true",
                        help='Treat a dir as a project and concat files together')
    parser.add_argument('-r', action="store_true",
                        help='Randomly pick one instruction/output so that each input is unique in the generated JSON')
    parser.add_argument('--input', metavar='INPUT_DIR', required=True, type=str,
                        help='Input directory of source files')
    parser.add_argument('--out', metavar="OUT_FILE")

    args = parser.parse_args()
    init_logging(args.l)
    logger.debug(args)
    return args


if __name__ == "__main__":
    main()
