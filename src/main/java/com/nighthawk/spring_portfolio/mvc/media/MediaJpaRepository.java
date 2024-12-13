
package com.nighthawk.spring_portfolio.mvc.media;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



public interface MediaJpaRepository extends JpaRepository<Media, Long> {

    void save(int score);

    List<Media> findAllByScoreInc();

    @Query("SELECT m FROM Media m ORDER BY m.score DESC")
    List<Media> findFirstPlaceInfo();

    @Query("SELECT m FROM Media m WHERE m.personId = :personId")
    List<Media> findByPersonId(Long person_id);
}