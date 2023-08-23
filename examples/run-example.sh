#!/usr/bin/env bash
set -eux
java -jar ../target/genMutator-1.0-SNAPSHOT-jar-with-dependencies.jar -i type1-proj -o .out -n 5
