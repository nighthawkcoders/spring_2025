package com.nighthawk.spring_portfolio.mvc.mines;

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
        
        user.setBalance(user.getBalance() + board.winnings() * betSize);

        personJpaRepository.save(user);
        return new ResponseEntity<>(user.getBalance(), HttpStatus.OK);
    }

    @PostMapping("/stakes/{stakes}")
    public ResponseEntity<String> postStakes(@PathVariable String stakes, @RequestBody MinesRequest minesRequest) {
        Person user = personJpaRepository.findByEmail(minesRequest.getEmail());
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    
        double betSize = minesRequest.getBetSize();
        if (user.getBalance() < betSize) {
            return new ResponseEntity<>("Insufficient balance", HttpStatus.BAD_REQUEST);
        }
    
        user.setBalance(user.getBalance() - betSize);
        personJpaRepository.save(user);
    
        board = new MinesBoard(stakes);
    
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @GetMapping("/balance/{email}")
    public ResponseEntity<Double> getBalance(@PathVariable String email) {
    Person user = personJpaRepository.findByEmail(email);
    if (user == null) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(user.getBalance(), HttpStatus.OK);
    }
}
