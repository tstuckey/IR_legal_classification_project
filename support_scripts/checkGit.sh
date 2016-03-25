#!/bin/bash
#
mypath=~thomasstuckey/Google\ Drive

#get to where the script is invoked from
parent_path=$( cd "$(dirname "${BASH_SOURCE}")" ; pwd -P )
cd "$parent_path"
#and then get to the right relative path
cd "$mypath"

#look for directories only named .git
find . -iname \.git -type d -print0 | while IFS= read -r -d '' valid_directory;
do
  printf "Valid Git Directory: ""$valid_directory%s\n"
  #push current directory to the stack
  pushd .
  #change to directory with the .git folder and then go one to the relative parent director before running
  #the git commands
  cd "$valid_directory"/..
  git pull
  git status
  popd
done
