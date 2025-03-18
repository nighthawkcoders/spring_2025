package com.nighthawk.spring_portfolio.hacks.tablesaw;


import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.File;

public class TitanicML {
    public static void main(String[] args) throws Exception {
        // Load dataset
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File("src/main/resources/data/titanic_cleaned.csv"));
        Instances data = loader.getDataSet();
        
        // Set class index to the last column (Survived)
        data.setClassIndex(data.numAttributes() - 1);

        // Split dataset into training (70%) and testing (30%)
        int trainSize = (int) Math.round(data.numInstances() * 0.7);
        int testSize = data.numInstances() - trainSize;
        Instances trainData = new Instances(data, 0, trainSize);
        Instances testData = new Instances(data, trainSize, testSize);

        // Train Decision Tree (J48)
        Classifier dt = new J48();
        dt.buildClassifier(trainData);
        System.out.println("Decision Tree Model:\n" + dt);

        // Train Logistic Regression
        Classifier logReg = new Logistic();
        logReg.buildClassifier(trainData);
        System.out.println("Logistic Regression Model:\n" + logReg);

        // Test models
        System.out.println("\nTesting Decision Tree:");
        evaluateModel(dt, testData);

        System.out.println("\nTesting Logistic Regression:");
        evaluateModel(logReg, testData);
    }

    private static void evaluateModel(Classifier model, Instances testData) throws Exception {
        int correct = 0;
        for (int i = 0; i < testData.numInstances(); i++) {
            Instance instance = testData.instance(i);
            double predicted = model.classifyInstance(instance);
            if (predicted == instance.classValue()) {
                correct++;
            }
        }
        double accuracy = (double) correct / testData.numInstances() * 100;
        System.out.printf("Accuracy: %.2f%%\n", accuracy);
    }
}
