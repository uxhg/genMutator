#!/usr/bin/env bash
set -eu


# Directory containing the Python helper scripts
SCRIPT_DIR=$PWD

# User-specified input directory and output directory
INPUT_DIR="$1"
OUTPUT_DIR="$2"

# Check if input and output directories are provided
if [ -z "$INPUT_DIR" ] || [ -z "$OUTPUT_DIR" ]; then
    echo "Usage: $0 <input_directory> <output_directory>"
    exit 1
fi

# Mkdir if not exist
mkdir -p "$OUTPUT_DIR"

# Iterate through directories cfg1 to cfg6
for i in {1..6}; do
    DIRECTORY="cfg$i"
    INPUT_PATH="$INPUT_DIR/$DIRECTORY"
    OUTPUT_FILE="$OUTPUT_DIR/cfg$i.json"
    
    # Run the Python script with the specified parameters
    python3 "$SCRIPT_DIR/main.py" -l INFO --input "$INPUT_PATH" --out "$OUTPUT_FILE"
    
    echo "Completed processing $DIRECTORY"
done



