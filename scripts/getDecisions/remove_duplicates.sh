#!/bin/bash
#
mypath=../../data/datasources-converted

#get to where the script is invoked from
parent_path=$( cd "$(dirname "${BASH_SOURCE}")" ; pwd -P )
cd "$parent_path"
#and then get to the right relative path
cd "$mypath"

find . -iname \*\(1\).txt -type f -print0 | while IFS= read -r -d '' dupfile;
   do
        rm "$dupfile"
   done
