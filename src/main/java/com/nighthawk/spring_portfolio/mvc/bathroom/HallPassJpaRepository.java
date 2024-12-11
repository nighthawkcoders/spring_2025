package com.nighthawk.spring_portfolio.mvc.bathroom;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface HallPassJpaRepository extends CrudRepository<HallPass, String> {
    Optional<HallPass> findByPersonIdAndCheckoutIsNull(String student_email);
}
