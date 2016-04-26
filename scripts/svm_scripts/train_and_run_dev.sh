#!/bin/bash

cd ../../svm_light/

./svm_learn -j 10 ../lookupInfo/data.train ../lookupInfo/model
./svm_classify ../lookupInfo/data.dev ../lookupInfo/model ../lookupInfo/predictions