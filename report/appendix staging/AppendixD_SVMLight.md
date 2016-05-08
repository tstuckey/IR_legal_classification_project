# Appendix D SVM<sup>Light</sup> Invocation  

## Train the Models and Validate the Models
```bash
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
```  

## Train the Models and get predictions on the Test Set  
```bash
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
```


