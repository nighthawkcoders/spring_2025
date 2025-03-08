package com.nighthawk.spring_portfolio.hacks.tablesaw;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.NumericColumn;

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
        BooleanColumn aloneColumn = BooleanColumn.create("Alone", titanic.rowCount());
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
        
        // Filter the data into two tables: perished and survived
        Table perished = titanic.where(titanic.numberColumn("Survived").isEqualTo(0));
        Table survived = titanic.where(titanic.numberColumn("Survived").isEqualTo(1));

        // Conclusions:
        // 1. Gender analysis: Check for survival based on "Sex" column
        System.out.println("\nSurvival rate by gender:");
        System.out.println("Males survived: " + perished.where(survived.stringColumn("Sex").isEqualTo("male")).rowCount());
        System.out.println("Males perished: " + perished.where(perished.stringColumn("Sex").isEqualTo("male")).rowCount());
        System.out.println("Females survived: " + perished.where(survived.stringColumn("Sex").isEqualTo("female")).rowCount());
        System.out.println("Females perished: " + perished.where(perished.stringColumn("Sex").isEqualTo("female")).rowCount());

        // 2. Fare analysis: Check survival based on "Fare" mean and median
        System.out.println("\nSurvival rate based on Fare Analysis:");
        System.out.println("Mean Fare for Survivors: " + survived.numberColumn("Fare").mean());
        System.out.println("Median Fare for Survivors: " + survived.numberColumn("Fare").median());
        System.out.println("Mean Fare for Non-Survivors: " + perished.numberColumn("Fare").mean());
        System.out.println("Median Fare for Non-Survivors: " + perished.numberColumn("Fare").median()); 

        // 3. Being alone analysis: Check survival based on "Alone" column
        System.out.println("\nSurvival Rate Based on 'Alone' Status:");
        System.out.println("Survived Alone: " + survived.where(survived.booleanColumn("Alone").isTrue()).rowCount());
        System.out.println("Perished Alone: " + perished.where(perished.booleanColumn("Alone").isTrue()).rowCount());
        System.out.println("Survived with Family: " + survived.where(survived.booleanColumn("Alone").isFalse()).rowCount());
        System.out.println("Perished with Family: " + perished.where(perished.booleanColumn("Alone").isFalse()).rowCount());
    }
}