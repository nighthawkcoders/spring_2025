package com.nighthawk.spring_portfolio.mvc.jokes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data 
@NoArgsConstructor
@AllArgsConstructor
@Entity 
public class Scores {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) 

}
