// simplifies database operations by providing built-in methods for common tasks (CRUD) and decreases amount of repetitive code
package com.nighthawk.spring_portfolio.mvc.rpg.streak;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // this is a repositary and is responsible for the database access
public interface StreakJpaRepository extends JpaRepository<Streak, Long> {
// declares SJP as interface
    // Method to find a single streak by userId, returns a streak if found or empty result if not found
    Optional<Streak> findByUserId(Long userId);

    // Method to find streaks by userId or maxStreak, it can return a list if multiple entries have the same numbers (if needed for more complex queries)
    List<Streak> findByUserIdOrMaxStreak(Long userId, int maxStreak);
}
