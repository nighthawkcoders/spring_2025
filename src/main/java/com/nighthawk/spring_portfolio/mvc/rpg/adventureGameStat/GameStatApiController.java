// define package and import necessary libraries
package com.nighthawk.spring_portfolio.mvc.rpg.adventureGameStat;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;
import com.nighthawk.spring_portfolio.mvc.rpg.gamifyGame.Game;
import com.nighthawk.spring_portfolio.mvc.rpg.gamifyGame.GameJpaRepository;

import lombok.Data;



// define rest controller and set base endpoint mapping
@RestController
@RequestMapping("/game")
public class GameStatApiController {

    @Autowired
    private GameJpaRepository gameJpaRepository;
    @Autowired
    private PersonJpaRepository personJpaRepository;
    @Autowired
    private GameStatJpaRepository gameStatJpaRepository;

    @Data
    public class GameStatDTO {
        private String personUid;  
        private String gameId;     
        private Map<String, Object> stats; 
    }

    @PostMapping("/createStats")
    public ResponseEntity<GameStat> createGameStat(@RequestBody GameStatDTO dto) {
        // Convert string uid to Person entity
        Person person = personJpaRepository.findByUid(dto.getPersonUid());
        if (person == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        Long gameId;
        try {
            gameId = Long.parseLong(dto.getGameId());
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Game game = gameJpaRepository.findById(gameId).orElse(null);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Create a new GameStat using the domain model's factory method
        GameStat gameStat = GameStat.createGameStats(person, game, dto.getStats());

        // Save the newly created GameStat to the database
        gameStat = gameStatJpaRepository.save(gameStat);

        // Return the created object with a 201 Created status
        return new ResponseEntity<>(gameStat, HttpStatus.CREATED);
    }


    @GetMapping("/getStats/{uid}")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable String uid) {
        // Retrieve the GameStat object by the user UID
        GameStat gamestat = gameStatJpaRepository.findByUid_uid(uid);
        
        // If no GameStat is found, return 404 Not Found
        if (gamestat == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        // Get the stats map from the GameStat object
        Map<String, Object> stats = gamestat.getStats();
        
        // Return the stats map with a 200 OK status
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @Data
    public static class updateStatsDTO {
        private Map<String, Object> stats;
    }

    @PostMapping("/updateStats/{uid}")
    public ResponseEntity<Map<String, Object>> updateStats(@RequestBody updateStatsDTO updateStatsDTO, @PathVariable String uid) {
        // Retrieve the GameStat object by the user's uid (using a repository method that navigates the Person object)
        GameStat gamestat = gameStatJpaRepository.findByUid_uid(uid);
        
        if (gamestat == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        // Update the stats from the DTO
        gamestat.setStats(updateStatsDTO.getStats());
        
        // Save the updated GameStat to the database
        gameStatJpaRepository.save(gamestat);
        
        // Return the updated stats map
        return new ResponseEntity<>(gamestat.getStats(), HttpStatus.OK);
    }
}