package com.nighthawk.spring_portfolio.mvc.media;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaJpaRepository extends JpaRepository<Scores, Long> {
    /* JPA has many built in methods: https://www.tutorialspoint.com/spring_boot_jpa/spring_boot_jpa_repository_methods.htm
    The below custom methods are prototyped for this application
    */
    void save(String Joke);
    List<Scores> findAllByScoreInc();
}
