package com.nighthawk.spring_portfolio.mvc.bathroom;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface  TeacherJpaRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findAllByOrderByFirstnameAsc();
    List<Teacher> findByFirstnameIgnoreCaseAndLastnameIgnoreCase(String firstname, String lastname);
}
