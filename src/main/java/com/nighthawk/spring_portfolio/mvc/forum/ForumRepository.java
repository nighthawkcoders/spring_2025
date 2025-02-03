package com.nighthawk.spring_portfolio.mvc.forum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumRepository extends JpaRepository<ForumTableController, Long> {
    // You can define custom queries if needed
}
