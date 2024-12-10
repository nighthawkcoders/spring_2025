package com.nighthawk.spring_portfolio.mvc.mines;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;

import lombok.Getter;

@RestController
@RequestMapping("/api/casino/mines")
public class MinesApiController {
    private MinesBoard board;

    @Getter
    public static class MinesRequest {
        private double betSize;
        private String email;
    }

    @Autowired
    private PersonJpaRepository personJpaRepository;

    @GetMapping("/{xCoord}/{yCoord}")
    public ResponseEntity<Boolean> getMine(@PathVariable int xCoord, @PathVariable int yCoord) {
        return new ResponseEntity<>(board.checkMine(xCoord, yCoord), HttpStatus.OK);
    }

    @PostMapping("/winnings")
    public ResponseEntity<Double> calculateWinnings(@RequestBody MinesRequest minesRequest) {
        Person user = personJpaRepository.findByEmail(minesRequest.getEmail());

        double betSize = minesRequest.getBetSize();
        if (user.getBalance() < betSize) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        user.setBalance(user.getBalance() - betSize + board.winnings() * betSize);

        personJpaRepository.save(user);
        return new ResponseEntity<>(user.getBalance(), HttpStatus.OK);
    }

    @PostMapping("/stakes/{stakes}")
    public ResponseEntity<String> postStakes(@PathVariable String stakes) {
        board = new MinesBoard(stakes);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
