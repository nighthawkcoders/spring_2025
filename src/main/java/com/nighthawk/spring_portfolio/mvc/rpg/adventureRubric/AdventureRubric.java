package com.nighthawk.spring_portfolio.mvc.rpg.adventureRubric;

import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AdventureRubric {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    // rubric uid
    @Column(unique = true, nullable = false)
    private String ruid;

    @Lob 
    @Column(columnDefinition = "TEXT") 
    private String criteria;

    public AdventureRubric (String ruid, String criteria) {
        this.ruid = ruid;
        this.criteria = criteria;
    }    

    public static AdventureRubric createRubric(String ruid, String criteria) {
        AdventureRubric rubric = new AdventureRubric();
        rubric.setRuid(ruid);
        rubric.setCriteria(criteria);

        return rubric;
    }

    public static AdventureRubric[] init() {
        ArrayList<AdventureRubric> rubrics = new ArrayList<>();
        
        // byte[] badgeIcon = loadImageAsByteArray("path/to/your/image.png");
        rubrics.add(createRubric(null, """
                {
    "max_score": 20,
    "grading_criteria": [
        { "category": "Accuracy", "weight": 0.5, "description": "Correctness of the response" },
        { "category": "Completeness", "weight": 0.3, "description": "How well the response covers all required points" },
        { "category": "Clarity", "weight": 0.2, "description": "How well-explained and structured the response is" }
    ]
}
                """));


        return rubrics.toArray(new AdventureRubric[0]);
    }
}

