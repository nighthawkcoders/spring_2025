package com.nighthawk.spring_portfolio.hacks.tablesaw;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.NumericColumn;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.InputStream;

public class TitanicAnalysis {
    public static void main(String[] args) throws Exception {
        // Load Titanic dataset from the classpath into a Tablesaw Table
        InputStream inputStream = TitanicAnalysis.class.getResourceAsStream("/data/titanic.csv");
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: titanic.csv");
        }
        Table titanic = Table.read().csv(inputStream);

        // Add "alone" column based on "SibSp (Siblings/Spouses)" and "Parch (Parents/Children)"
        NumericColumn<?> sibSpColumn = titanic.numberColumn("SibSp");
        NumericColumn<?> parchColumn = titanic.numberColumn("Parch");
        BooleanColumn aloneColumn = BooleanColumn.create("alone", titanic.rowCount());
        // Add a new column for each row, indicating whether the passenger is alone
        for (int i = 0; i < titanic.rowCount(); i++) {
            // If both SibSp and Parch are 0, then the passenger is alone otherwise not
            // Number casting is required because Tablesaw does not support primitive types
            boolean isAlone = ((Number) sibSpColumn.get(i)).doubleValue() == 0 && ((Number) parchColumn.get(i)).doubleValue() == 0;
            aloneColumn.set(i, isAlone);
        }
        titanic.addColumns(aloneColumn);

        // Display structure and first rows
        System.out.println(titanic.structure());
        System.out.println(titanic.first(5));

        // Median values
        System.out.println("\nMedian values:");
        // Tablesaw does not have a built-in median method, so we calculate it manually
        double[] fares = titanic.numberColumn("Fare").asDoubleArray();
        DescriptiveStatistics stats = new DescriptiveStatistics(fares);
        System.out.println("Median Fare: " + stats.getPercentile(50));

        // Mean values for survivors and non-survivors
        Table perished = titanic.where(titanic.numberColumn("Survived").isEqualTo(0));
        Table survived = titanic.where(titanic.numberColumn("Survived").isEqualTo(1));

        System.out.println("\nPerished Mean/Average:");
        double[] perishedFares = perished.numberColumn("Fare").asDoubleArray();
        DescriptiveStatistics perishedStats = new DescriptiveStatistics(perishedFares);
        System.out.println("Mean Fare: " + perishedStats.getMean());

        System.out.println("\nSurvived Mean/Average:");
        double[] survivedFares = survived.numberColumn("Fare").asDoubleArray();
        DescriptiveStatistics survivedStats = new DescriptiveStatistics(survivedFares);
        System.out.println("Mean Fare: " + survivedStats.getMean());

        // Max and Min values for survivors
        System.out.println("\nMaximums for survivors:");
        System.out.println("Max Fare: " + survivedStats.getMax());

        System.out.println("\nMinimums for survivors:");
        System.out.println("Min Fare: " + survivedStats.getMin());

        // Conclusions:
        // 1. Gender analysis: Check survival rates based on sex column
        Table maleSurvivors = survived.where(survived.numberColumn("Sex").isEqualTo(1));
        Table femaleSurvivors = survived.where(survived.numberColumn("Sex").isEqualTo(0));

        System.out.println("\nSurvival rate by gender:");
        System.out.println("Males survived: " + maleSurvivors.rowCount());
        System.out.println("Females survived: " + femaleSurvivors.rowCount());

        // 2. Fare analysis: Higher fares tend to correlate with survival
        System.out.println("\nMean Fare for Survivors: " + survivedStats.getMean());
        System.out.println("Mean Fare for Non-Survivors: " + perishedStats.getMean());

        // 3. Being alone analysis: Check survival based on "alone" column
        System.out.println("\nSurvival Rate Based on 'Alone' Status:");
        System.out.println("Survived Alone: " + survived.where(survived.booleanColumn("alone").isTrue()).rowCount());
        System.out.println("Survived with Family: " + survived.where(survived.booleanColumn("alone").isFalse()).rowCount());
    }
}