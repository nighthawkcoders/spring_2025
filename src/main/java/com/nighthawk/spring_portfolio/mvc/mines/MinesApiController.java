/**
 * API Controller for Mines Casino Game.
 * This controller provides endpoints to interact with a Minesweeper-like game,
 * including checking mines, calculating winnings, posting stakes, and retrieving user balance.
 */
package com.nighthawk.spring_portfolio.mvc.mines;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/casino/mines")
public class MinesApiController {
    private final PersonJpaRepository personJpaRepository;
    private MinesBoard board;

    /**
     * Data class representing a request to the Mines API.
     * Contains the bet size and user email.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MinesRequest {
        private double betSize;
        private String email;
    }

    /**
     * Checks if a user has sufficient balance to place a bet.
     * @param user The user placing the bet.
     * @param betSize The size of the bet.
     * @return True if the user has sufficient balance, false otherwise.
     */
    private boolean hasSufficientBalance(Person user, double betSize) {
        if (user.getBalance() < betSize) {
            log.warn("Insufficient balance for user {}. Bet size: {}, Balance: {}", user.getEmail(), betSize, user.getBalance());
            return false;
        }
        return true;
    }

    /**
     * Checks if a mine exists at specified coordinates.
     * @param xCoord The x-coordinate of the mine.
     * @param yCoord The y-coordinate of the mine.
     * @return ResponseEntity containing true if a mine exists, false otherwise.
     */
    @GetMapping("/{xCoord}/{yCoord}")
    public ResponseEntity<Boolean> getMine(@PathVariable int xCoord, @PathVariable int yCoord) {
        log.info("Checking mine at coordinates ({}, {})", xCoord, yCoord);
        return new ResponseEntity<>(board.checkMine(xCoord, yCoord), HttpStatus.OK);
    }

    /**
     * Calculates the winnings for a bet and updates the user's balance.
     * @param minesRequest The request containing bet details.
     * @return ResponseEntity containing the updated balance or an error status.
     */
    @PostMapping("/winnings")
    public ResponseEntity<Double> calculateWinnings(@RequestBody MinesRequest minesRequest) {
        Person user = personJpaRepository.findByEmail(minesRequest.getEmail());
        double betSize = minesRequest.getBetSize();
        if (!hasSufficientBalance(user, betSize)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        user.setBalance(user.getBalance() + board.winnings() * betSize);
        personJpaRepository.save(user);
        return new ResponseEntity<>(user.getBalance(), HttpStatus.OK);
    }

    /**
     * Initializes a new MinesBoard with specified stakes and deducts the bet from the user's balance.
     * @param stakes The stakes configuration for the game.
     * @param minesRequest The request containing bet details.
     * @return ResponseEntity indicating success or an error status.
     */
    @PostMapping("/stakes/{stakes}")
    public ResponseEntity<String> postStakes(@PathVariable String stakes, @RequestBody MinesRequest minesRequest) {
        Person user = personJpaRepository.findByEmail(minesRequest.getEmail());
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        double betSize = minesRequest.getBetSize();
        if (!hasSufficientBalance(user, betSize)) {
            return new ResponseEntity<>("Insufficient balance", HttpStatus.BAD_REQUEST);
        }

        user.setBalance(user.getBalance() - betSize);
        personJpaRepository.save(user);
        board = new MinesBoard(stakes);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Retrieves the balance of a user by email.
     * @param email The email of the user.
     * @return ResponseEntity containing the user's balance or an error status if the user is not found.
     */
    @GetMapping("/balance/{email}")
    public ResponseEntity<Double> getBalance(@PathVariable String email) {
        Person user = personJpaRepository.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user.getBalance(), HttpStatus.OK);
    }
}
