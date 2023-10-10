import argparse
import json
import logging
from pathlib import Path
from typing import Dict, List

from util import init_logging

logger = logging.getLogger(__name__)


def main():
    args = handle_args()


def handle_args():
    parser = argparse.ArgumentParser(description='Convert generated source files to JSON')
    parser.add_argument('-l', metavar='LOG_LEVEL', type=str)
    parser.add_argument('--input', metavar='INPUT_DIR', type=str, help='Input directory of source files')
    parser.add_argument('--out', metavar="OUT_DIR")

    args = parser.parse_args()
    init_logging(args.l)
    logger.debug(args)
    return args


if __name__ == "__main__":
    main()
