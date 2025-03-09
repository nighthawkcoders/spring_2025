package com.nighthawk.spring_portfolio.hacks.tablesaw;

import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;
import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.J48;
import weka.core.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TitanicML {

    public static void main(String[] args) throws Exception {
        // Step 1: Load and Clean Data using Tablesaw
        InputStream inputStream = TitanicML.class.getResourceAsStream("/data/titanic.csv");
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: titanic.csv");
        }
        Table titanic = Table.read().csv(inputStream);

        // Drop non-relevant columns
        titanic = titanic.removeColumns("Name", "Ticket", "Cabin");

        // Convert categorical values to numeric
        StringColumn sex = titanic.stringColumn("Sex");
        sex = sex.replaceAll("male", "1").replaceAll("female", "0");
        titanic.replaceColumn("Sex", sex);

        StringColumn embarked = titanic.stringColumn("Embarked");
        embarked = embarked.replaceAll("S", "0").replaceAll("C", "1").replaceAll("Q", "2");
        titanic.replaceColumn("Embarked", embarked);

        // Fill missing values
        titanic.doubleColumn("Age").setMissingTo(titanic.numberColumn("Age").median());
        titanic.doubleColumn("Fare").setMissingTo(titanic.numberColumn("Fare").median());

        // Step 2: Convert Tablesaw Table to Weka Instances
        Instances data = convertTableToWeka(titanic);

        // Set class index (target variable)
        data.setClassIndex(data.attribute("Survived").index());

        // Step 3: Apply Machine Learning Models
        J48 tree = new J48();
        tree.buildClassifier(data);

        Logistic logistic = new Logistic();
        logistic.buildClassifier(data);

        // Step 4: Evaluate Models
        System.out.println("Decision Tree Model:\n" + tree);
        System.out.println("Logistic Regression Model:\n" + logistic);

        // Cross-validation evaluation
        evaluateModel(tree, data, "Decision Tree");
        evaluateModel(logistic, data, "Logistic Regression");
    }

    // Convert Tablesaw Table to Weka Instances (Fixed Casting Issue)
    private static Instances convertTableToWeka(Table table) {
        List<Attribute> attributes = new ArrayList<>();

        // Define attributes based on column types
        for (Column<?> col : table.columns()) {
            if (col.type().equals(ColumnType.STRING)) {
                List<String> classValues = new ArrayList<>();
                table.stringColumn(col.name()).unique().forEach(classValues::add);
                attributes.add(new Attribute(col.name(), classValues));
            } else {
                attributes.add(new Attribute(col.name()));
            }
        }

        // Create Weka dataset
        Instances data = new Instances("Titanic", new ArrayList<>(attributes), table.rowCount());

        for (Row row : table) {
            double[] values = new double[table.columnCount()];
            for (int i = 0; i < table.columnCount(); i++) {
                Column<?> col = table.column(i);

                // Handle Integer and Double columns
                if (col.type() == ColumnType.INTEGER) {
                    values[i] = row.getInt(i);
                } else if (col.type() == ColumnType.DOUBLE) {
                    values[i] = row.getDouble(i);
                }
            }
            data.add(new DenseInstance(1.0, values));
        }
        return data;
    }

    // Evaluate model using 10-fold cross-validation
    private static void evaluateModel(Classifier model, Instances data, String modelName) throws Exception {
        weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
        eval.crossValidateModel(model, data, 10, new java.util.Random(1));
        System.out.printf("%s Accuracy: %.2f%%%n", modelName, eval.pctCorrect());
    }
}
