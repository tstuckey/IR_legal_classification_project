#!/bin/bash

./svm_light_osx/svm_learn ../../data/output_data/train_affirm ../../data/learner_data/model_affirm
./svm_light_osx/svm_learn ../../data/output_data/train_remand ../../data/learner_data/model_remand
./svm_light_osx/svm_learn ../../data/output_data/train_reverse ../../data/learner_data/model_reverse
./svm_light_osx/svm_learn ../../data/output_data/train_reverse_and_remand ../../data/learner_data/model_reverse_and_remand
./svm_light_osx/svm_learn ../../data/output_data/train_unanimous ../../data/learner_data/model_unanimous

./svm_light_osx/svm_classify  ../../data/output_data/test_affirm ../../data/learner_data/model_affirm ../../data/learner_data/predictions_test_affirm
./svm_light_osx/svm_classify  ../../data/output_data/test_remand ../../data/learner_data/model_remand ../../data/learner_data/predictions_test_remand
./svm_light_osx/svm_classify  ../../data/output_data/test_reverse ../../data/learner_data/model_reverse ../../data/learner_data/predictions_test_reverse
./svm_light_osx/svm_classify  ../../data/output_data/test_reverse_and_remand ../../data/learner_data/model_reverse_and_remand ../../data/learner_data/predictions_test_reverse_and_remand
./svm_light_osx/svm_classify  ../../data/output_data/test_unanimous ../../data/learner_data/model_unanimous ../../data/learner_data/predictions_test_unanimous