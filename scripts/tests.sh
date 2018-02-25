#!/bin/bash
for folder in $(ls -d tests/*); do
  echo "Running $folder"
  ./solve $folder/repository.json $folder/initial.json $folder/constraints.json
done