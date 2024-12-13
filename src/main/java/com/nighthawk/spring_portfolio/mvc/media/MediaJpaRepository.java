
package com.nighthawk.spring_portfolio.mvc.media;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



public interface MediaJpaRepository extends JpaRepository<Media, Long> {

    @Query("SELECT m FROM Media m ORDER BY m.score ASC")
    List<Media> findAllByScoreInc();

    @Query("SELECT m FROM Media m ORDER BY m.score DESC")
    List<Media> findFirstPlaceInfo();

    List<Media> findByPersonId(Long personId);
}