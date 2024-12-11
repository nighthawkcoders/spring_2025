package com.nighthawk.spring_portfolio.mvc.person;

import org.springframework.data.jpa.repository.JpaRepository;

public interface  PersonSectionsJpaRepository extends JpaRepository<PersonSections, Long> {
    PersonSections findByName(String name);
}
