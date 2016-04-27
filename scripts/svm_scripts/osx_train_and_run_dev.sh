#!/bin/bash

./svm_light_osx/svm_learn ../../data/output_data/train_affirm ../../data/learner_data/model_affirm
./svm_light_osx/svm_learn ../../data/output_data/train_remand ../../data/learner_data/model_remand
./svm_light_osx/svm_learn ../../data/output_data/train_reverse ../../data/learner_data/model_reverse
./svm_light_osx/svm_learn ../../data/output_data/train_reverse_and_remand ../../data/learner_data/model_reverse_and_remand
./svm_light_osx/svm_learn ../../data/output_data/train_unanimous ../../data/learner_data/model_unanimous

./svm_light_osx/svm_classify  ../../data/output_data/dev_affirm ../../data/learner_data/model_affirm ../../data/learner_data/predictions_dev_affirm
./svm_light_osx/svm_classify  ../../data/output_data/dev_remand ../../data/learner_data/model_remand ../../data/learner_data/predictions_dev_remand
./svm_light_osx/svm_classify  ../../data/output_data/dev_reverse ../../data/learner_data/model_reverse ../../data/learner_data/predictions_dev_reverse
./svm_light_osx/svm_classify  ../../data/output_data/dev_reverse_and_remand ../../data/learner_data/model_reverse_and_remand ../../data/learner_data/predictions_dev_reverse_and_remand
./svm_light_osx/svm_classify  ../../data/output_data/dev_unanimous ../../data/learner_data/model_unanimous ../../data/learner_data/predictions_dev_unanimous