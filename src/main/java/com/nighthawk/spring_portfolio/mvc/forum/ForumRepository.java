package com.nighthawk.spring_portfolio.mvc.forum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ForumRepository extends JpaRepository<Forum, Long> {
    // You can define custom queries if needed
    Forum findByTitle(String title);
    //Forum findAllTitles(String title);
    //List<Forum> findByTitleContaining(String title);
    
}
