#!/bin/bash

cd ../../svm_light_windows.8.4/

./svm_learn.exe -j 10  ../lookupInfo/data.train ../lookupInfo/model
./svm_classify.exe ../lookupInfo/data.test ../lookupInfo/model ../lookupInfo/predictions