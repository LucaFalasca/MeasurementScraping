package it.lucafalasca.weka;


import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.converters.ConverterUtils.DataSource;

public class Classification {
    public static void claffify() throws Exception {
        Classifier classifier = new RandomForest();
        DataSource testSource = new DataSource("src/main/resources/csv_files/DATASET_BOOKKEEPER_TEST_2024_02_05@10_53_RELEASE_2.csv");


    }
}