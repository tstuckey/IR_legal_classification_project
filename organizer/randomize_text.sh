#!/usr/bin/env bash
#
mypath=../converted_datasources
mydestination=../converted_datasources

#get to where the script is invoked from
parent_path=$( cd "$(dirname "${BASH_SOURCE}")" ; pwd -P )
cd "$parent_path"
#and then get to the right relative path
cd "$mypath"

#printf "Valid Directory: ""$valid_directory%s\n"
find . -iname \*.txt -type f -print0 | while IFS= read -r -d '' file;
   do
        #rename=`echo "$file" | sed 's/\.pdf/\.txt/'`
        #printf "%s\t""$rename""%s\n"
        #filename_only=`basename "$rename"`
        #output="$mydestination"/"$filename_only"
        #printf "Destination file is ""$output""%s\n"
        #java -jar "$tikapath" --text "$file" > "$output"
        #shuf -r -n 10 -e H H H H H T
        printf "$file%s\n"
done
