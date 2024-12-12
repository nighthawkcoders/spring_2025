package com.nighthawk.spring_portfolio.mvc.messages;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nighthawk.spring_portfolio.mvc.person.PersonRole;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class saigaiComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(unique=true)
    private String content;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "message_id")
    private saigaiMessage message;

//     @ManyToMany(fetch = FetchType.EAGER)
// @JoinTable(
//     name = "person_roles",
//     joinColumns = @JoinColumn(name = "person_id"),
//     inverseJoinColumns = @JoinColumn(name = "role_id")
// )
// private Set<PersonRole> roles;
}