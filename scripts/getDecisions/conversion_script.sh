#!/bin/bash
#
mypath=../../data/datasources
mydestination=../../data/datasources-converted
tikapath=../../lib/tika-app-1.12.jar

#get to where the script is invoked from
parent_path=$( cd "$(dirname "${BASH_SOURCE}")" ; pwd -P )
cd "$parent_path"
#and then get to the right relative path
cd "$mypath"

find . -iname \*pdf -type d -print0 | while IFS= read -r -d '' valid_directory;
do
  #printf "Valid Directory: ""$valid_directory%s\n"
   find "$valid_directory" -iname \*.pdf -type f -print0 | while IFS= read -r -d '' file;
   do
        rename=`echo "$file" | sed 's/\.pdf/\.txt/'`
        #printf "%s\t""$rename""%s\n"
        filename_only=`basename "$rename"`
        output="$mydestination"/"$filename_only"
        printf "Destination file is ""$output""%s\n"
        java -jar "$tikapath" --text "$file" > "$output"
        printf "$file%s\n"
   done
done
