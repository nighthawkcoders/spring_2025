package com.nighthawk.spring_portfolio.mvc.blackjack;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;

import lombok.Getter;

@RestController
@RequestMapping("/api/casino/blackjack")
public class BlackjackApiController {

    @Autowired
    private PersonJpaRepository personJpaRepository;

    @Autowired
    private BlackjackJpaRepository blackjackJpaRepository;

    @Getter
    public static class BlackjackRequest {
        private String uid;
        private double betAmount;
    }

    @PostMapping("/start")
    public ResponseEntity<String> startGame(@RequestBody BlackjackRequest blackjackRequest) {
        try {
            String uid = blackjackRequest.getUid();
            double betAmount = blackjackRequest.getBetAmount();

            Person user = personJpaRepository.findByUid(uid);
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            Blackjack game = new Blackjack();
            game.setPerson(user);
            game.setStatus("ACTIVE");
            game.setBetAmount(betAmount);
            game.initializeDeck();
            game.dealInitialHands();
            blackjackJpaRepository.save(game);

            return new ResponseEntity<>("Game started successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error starting game: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/hit")
    public ResponseEntity<String> hit(@RequestBody BlackjackRequest blackjackRequest) {
        try {
            String uid = blackjackRequest.getUid();

            Person user = personJpaRepository.findByUid(uid);
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            Blackjack game = blackjackJpaRepository.findFirstByPersonAndStatusOrderByIdDesc(user, "ACTIVE").orElse(null);
            if (game == null) {
                return new ResponseEntity<>("No active game found", HttpStatus.NOT_FOUND);
            }

            game.loadGameState();

            List<String> playerHand = safeCastToList(game.getGameStateMap().get("playerHand"));
            List<String> deck = safeCastToList(game.getGameStateMap().get("deck"));

            if (deck.isEmpty()) {
                return new ResponseEntity<>("Deck is empty", HttpStatus.OK);
            }

            String drawnCard = deck.remove(0);
            playerHand.add(drawnCard);
            int playerScore = safeCastToInt(game.getGameStateMap().get("playerScore"));

            game.getGameStateMap().put("playerHand", playerHand);
            game.getGameStateMap().put("playerScore", playerScore);
            game.getGameStateMap().put("deck", deck);
            game.persistGameState();

            if (playerScore > 21) {
                double updatedBalance = user.getBalanceDouble() - game.getBetAmount();
                user.setBalanceString(updatedBalance);
                game.setStatus("INACTIVE");
                personJpaRepository.save(user);
            }

            blackjackJpaRepository.save(game);
            return new ResponseEntity<>("Hit successful!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error processing hit: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> safeCastToList(Object obj) {
        if (obj instanceof List<?> list) {
            return (List<String>) list;
        }
        return new ArrayList<>();
    }

    private int safeCastToInt(Object obj) {
        if (obj instanceof Integer) {
            return (int) obj;
        } else if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        return 0;
    }
}