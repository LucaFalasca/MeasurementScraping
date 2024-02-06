package it.lucafalasca.enumerations;

import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;

public enum ClassifierSupported {
    RANDOM_FOREST,
    IBK,
    NAIVE_BAYES;

    public Classifier getClassifier(){
        switch (this){
            case RANDOM_FOREST:
                return new RandomForest();
            case IBK:
                return new weka.classifiers.lazy.IBk();
            case NAIVE_BAYES:
                return new weka.classifiers.bayes.NaiveBayes();
            default:
                return null;
        }
    }


    @Override
    public String toString() {
        switch (this){
            case RANDOM_FOREST:
                return "RandomForest";
            case IBK:
                return "IBK";
            case NAIVE_BAYES:
                return "NaiveBayes";
            default:
                return "";
        }
    }
}