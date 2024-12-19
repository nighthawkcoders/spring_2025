package com.nighthawk.spring_portfolio.mvc.Grades;
import java.util.List;



import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsmtJpaRepository extends JpaRepository<Asmt, Long>{
    void save(String assignmentName);
    //Optional<Assignment> findByName(String assignmentName);

    //@Query("SELECT y FROM User u WHERE u.lastName = ?1")
    //List<User> findByLastNameQuery(String lastName);
}
