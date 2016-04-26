#!/bin/bash
#
mypath=../data/datasources-converted
mydestination=../data/datasources-partitioned

#get to where the script is invoked from
parent_path=$( cd "$(dirname "${BASH_SOURCE}")" ; pwd -P )
cd "$parent_path"
#and then get to the right relative path
cd "$mypath"

find . -iname \*.txt -type f -print0 | while IFS= read -r -d '' srcfile;
   do
        srcfilename_only=`basename "$srcfile"`
        target=`gshuf -e train train train train train train train train train train train train train train train dev dev dev test test test -n 1`
        #printf "$mydestination""/""$target""/""$srcfilename_only%s\n"
        #printf "$srcfile%s\n"
        destfile="$mydestination""/""$target""/""$srcfilename_only"
        cp "$srcfile" "$destfile"
   done
