#!/bin/bash
#Get the information from www.supremecourt.gov via wget

directoryPrefix=supremecourt_
script_name=get_data.sh
mypath=../datasources

#get to where the script is invoked from
parent_path=$( cd "$(dirname "${BASH_SOURCE}")" ; pwd -P )
cd "$parent_path"
#and then get to the right relative path
cd "$mypath"

#Lets iterate from 2003(oldest available) to 2015 (newest available)
#This is the prep work to make the request
for ((i=3;i<=15;i++));
  do
    if [ "$i" -lt "10" ]
      then
	      #put in a leading zero so we make correct HTTP request
	      year="0$i"
      else
	      year="$i"
    fi
    echo "$year"
    

    directoryname=$directoryPrefix$year
    #check to see if the directory exists	
    if [ -d "$directoryname" ]
      then
        #delete the existing directory
        rm -rf "$directoryname"
    fi
    #create a fresh directory
    mkdir "$directoryname"
    cd "$directoryname"

    #check to see if there is already an old copy of this script there 
    if [ -e "$script_name" ]
      then
      #delete the existing file
      rm "$script_name"
    fi
  
    if [ "$i" -le "11" ]
      #2003-to-2011 is done one way
      then
        #create the script to make the wget request
        printf "#!/bin/sh\n">$script_name
        printf "wget -r --accept=pdf http://www.supremecourt.gov/opinions/"$year"pdf">>$script_name

      #2012-to-2015 is done another way
      else
        #create the script to make the wget request
        printf "#!/bin/sh\n">$script_name
        printf "wget -r --accept=pdf http://www.supremecourt.gov/opinions/slipopinion/$year">>$script_name
    fi 

    chmod +x "$script_name"
    #Ok, let's run the script; note we are just running these sequentially
    ./"$script_name"
    cd ..
done
