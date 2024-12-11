package com.nighthawk.spring_portfolio.mvc.bathroom;
import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String bathroom;

    private String issue;
    
    private int count;

    public Issue(String bathroomName, String issue, int count)
    {
        this.bathroom = bathroomName;
        this.issue = issue;
        this.count = count;
    }

    public static Issue[] init()
    {
        ArrayList<Issue> issues = new ArrayList<>();
        issues.add(new Issue("D Building Bathroom", "No Door Lock", 0));
        issues.add(new Issue("D Building Bathroom", "No Toilet Paper", 0));
        issues.add(new Issue("D Building Bathroom", "No Stall Doors", 0));

        issues.add(new Issue("L Building", "No Door Lock", 0));
        issues.add(new Issue("L Building", "No Toilet Paper", 0));
        issues.add(new Issue("L Building", "No Stall Doors", 0));

        issues.add(new Issue("A Building", "No Door Lock", 0));
        issues.add(new Issue("A Building", "No Toilet Paper", 0));
        issues.add(new Issue("A Building", "No Stall Doors", 0));

        issues.add(new Issue("Football Field", "No Door Lock", 0));
        issues.add(new Issue("Football Field", "No Toilet Paper", 0));
        issues.add(new Issue("Football Field", "No Stall Doors", 0));

        issues.add(new Issue("Locker Room Bathrooms", "No Door Lock", 0));
        issues.add(new Issue("Locker Room Bathrooms", "No Toilet Paper", 0));
        issues.add(new Issue("Locker Room Bathroom", "No Stall Doors", 0));
        return issues.toArray(new Issue[0]);
    }
}
