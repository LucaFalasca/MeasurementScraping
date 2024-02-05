package it.lucafalasca.weka;


import it.lucafalasca.enumerations.EvaluationMetric;
import it.lucafalasca.enumerations.Experiment;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.CostMatrix;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.Resample;

import java.util.*;

public class Classification {
    private static void claffify() throws Exception {
        Classifier classifier = new RandomForest();
        DataSource testSource = new DataSource("src/main/resources/arff_files/ARFF_DATASET_BOOKKEEPER_RELEASE_2_TEST_2024_02_05@10_53.arff");
        DataSource trainSource = new DataSource("src/main/resources/arff_files/ARFF_DATASET_BOOKKEEPER_RELEASE2_2024_02_05@10_53.arff");
        Instances train = trainSource.getDataSet();
        Instances test = testSource.getDataSet();

        train.setClassIndex(train.numAttributes() - 1);
        test.setClassIndex(test.numAttributes() - 1);

        classifier.buildClassifier(train);
        Evaluation evaluation = new Evaluation(test);
        evaluation.evaluateModel(classifier, test);
        System.out.println("AUC: " + evaluation.areaUnderROC(1));
        System.out.println("Precision: " + evaluation.precision(1));
        System.out.println("Recall: " + evaluation.recall(1));

    }
    //Bisogna settare il class index prima di chiamare questo metodo
    private static Map<EvaluationMetric, Double> classify(Instances trainingSet, Instances testingSet, Classifier classifier) throws Exception {
        classifier.buildClassifier(trainingSet);
        Evaluation evaluation = new Evaluation(testingSet);
        evaluation.evaluateModel(classifier, testingSet);
        Map<EvaluationMetric, Double> ret = new HashMap<>();
        ret.put(EvaluationMetric.AUC, evaluation.areaUnderROC(1));
        ret.put(EvaluationMetric.PRECISION, evaluation.precision(1));
        ret.put(EvaluationMetric.RECALL, evaluation.recall(1));
        ret.put(EvaluationMetric.KAPPA, evaluation.kappa());
        return ret;
    }

    public static Map<EvaluationMetric, Double> classify(Instances trainingSet, Instances testingSet, Classifier classifier, Experiment experiment) throws Exception {
        switch(experiment){
            case NOTHING -> {
                return classify(trainingSet, testingSet, classifier);
            }
            case BEST_FIRST -> {
                Instances[] filteredSets = featureSelection(trainingSet, testingSet);
                return classify(filteredSets[0], filteredSets[1], classifier);
            }
            case OVERSAMPLING -> {
                String samplePercent = overSampling(trainingSet);
                Resample resample = new Resample();
                resample.setOptions(new String[] {"-B", "1.0", "-S", "1", "-Z", samplePercent});
                FilteredClassifier fc = new FilteredClassifier();
                fc.setFilter(resample);
                fc.setClassifier(classifier);
                return classify(trainingSet, testingSet, fc);
            }
            case SENSITIVE_LEARNING -> {
                CostSensitiveClassifier csc = new CostSensitiveClassifier();
                csc.setMinimizeExpectedCost(false);
                CostMatrix cm = getCostMatrix(1.0, 10.0);
                csc.setCostMatrix(cm);
                csc.setClassifier(classifier);
                return classify(trainingSet, testingSet, csc);
            }
            default -> {
                throw new IllegalArgumentException("Invalid experiment");
            }
        }
    }

    private static CostMatrix getCostMatrix(double cfp, double cfn){
        CostMatrix costMatrix = new CostMatrix(2);
        costMatrix.setCell(0, 0, 0.0); //Costo true positive
        costMatrix.setCell(1, 0, cfp); //Costo false positive
        costMatrix.setCell(0, 1, cfn); //Costo false negative
        costMatrix.setCell(1, 1, 0.0); //Costo true negative
        return costMatrix;
    }

    private static String overSampling(Instances trainingSet) {
        double ret;
        int positive=0;
        int negative=0;
        for (weka.core.Instance instance : trainingSet) {
            if(Objects.equals(instance.toString(trainingSet.classIndex()), "1")) positive++;
            else negative++;
        }
        assert negative !=0;
        assert positive !=0;
        if(positive>negative) ret= 100*(positive - negative)/(double)negative;
        else ret= 100*(negative - positive)/(double)positive;
        return String.valueOf(ret);
    }

    private static Instances[] featureSelection(Instances trainingSet, Instances testingSet) throws Exception {
        CfsSubsetEval subsetEval = new CfsSubsetEval();
        BestFirst bestFirstSrc = new BestFirst();
        bestFirstSrc.setDirection(new SelectedTag(BestFirst.TAGS_SELECTION[2].getID(), BestFirst.TAGS_SELECTION)); //Bidirectional ASSUNZIONE 24
        bestFirstSrc.setSearchTermination(10);
        AttributeSelection filter = new AttributeSelection();

        filter.setEvaluator(subsetEval);
        filter.setSearch(bestFirstSrc);
        filter.setInputFormat(trainingSet);
        Instances filteredTrainingSet = Filter.useFilter(trainingSet, filter);
        Instances filteredTestingSet = Filter.useFilter(testingSet, filter);

        //Set attribute of interest, the buggyness
        int numAttr = filteredTrainingSet.numAttributes();
        filteredTrainingSet.setClassIndex(numAttr - 1);
        filteredTestingSet.setClassIndex(numAttr - 1);
        return new Instances[]{filteredTrainingSet, filteredTestingSet};
    }
}