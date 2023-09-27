#!/usr/bin/env bash
set -eu
# set -x

# Default values
LOG_LEVEL="info"
OUTPUT_DIR="."
INPUT_DIR="/home/wuxh/Projects/ai-assist-prog/ai-assist-prog-ncs/ncs-dataset-create/SyntheticCodeSamples/type3"
NUM_CFG=6
NUM_ITER=1000

# Parse cmdline options
while getopts ":l:o:i:n:" opt; do
  case $opt in
    l)
      LOG_LEVEL="$OPTARG"
      ;;
    o)
      OUTPUT_DIR="$OPTARG"
      ;;
    i)
      INPUT_DIR="$OPTARG"
      ;;
    n)
      NUM_ITER="$OPTARG"
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
      ;;
    :)
      echo "Option -$OPTARG requires an argument." >&2
      exit 1
      ;;
  esac
done

# Shift command-line arguments
shift $((OPTIND-1))


# Loop through mut1.json to mut6.json and cfg1 to cfg6
for i in $(seq 1 $NUM_CFG); do
    MUT_FILE="mut$i.json"
    CFG_DIR="${OUTPUT_DIR}/cfg$i"

    # Execute the command with the current MUT_FILE and CFG_DIR
    java -Dlog4j2.level="$LOG_LEVEL" -jar target/genMutator-1.0-SNAPSHOT-jar-with-dependencies.jar \
        -i "$INPUT_DIR" -c "$MUT_FILE" -o "$CFG_DIR" -n "$NUM_ITER"
done


