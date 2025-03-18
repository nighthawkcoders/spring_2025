package com.nighthawk.spring_portfolio.mvc.rpg.gamifyGame;



import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = false, nullable = true)
    private String name;
    
    public Game(String name) {
        this.name = name;
  
    }

    public static Game createGame(String name) {
        Game game = new Game();
        game.setName(name);

        return game;
    }

    public static Game[] init() {
        ArrayList<Game> games = new ArrayList<>();
        games.add(createGame("Adventure"));
        games.add(createGame("Casino"));
        games.add(createGame("Stocks"));
        games.add(createGame("Crypto"));
        return games.toArray(new Game[0]);
    }
}
