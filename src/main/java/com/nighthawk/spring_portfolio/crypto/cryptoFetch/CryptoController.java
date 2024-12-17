package com.nighthawk.spring_portfolio.crypto.cryptoFetch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;
import com.nighthawk.spring_portfolio.mvc.userStocks.UserStocksRepository;
import com.nighthawk.spring_portfolio.mvc.userStocks.userStocksTable;

@RestController
@RequestMapping("/api/crypto")
public class CryptoController {

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private PersonJpaRepository personRepository;

    @Autowired
    private UserStocksRepository userStocksRepo;

    // Endpoint to get the live cryptocurrency price
    @GetMapping("/price")
    public ResponseEntity<?> getCryptoPrice(@RequestParam String cryptoId) {
        double price = cryptoService.getCryptoPrice(cryptoId);
        if (price < 0) {
            return ResponseEntity.status(404).body("Price not found for crypto ID: " + cryptoId);
        }
        return ResponseEntity.ok("{ \"cryptoId\": \"" + cryptoId + "\", \"price\": " + price + " }");
    }

    // Endpoint to allow users to buy cryptocurrency
    @PostMapping("/buy")
    public ResponseEntity<?> buyCrypto(@RequestBody BuyRequest buyRequest) {
        String email = buyRequest.getEmail();
        String cryptoId = buyRequest.getCryptoId();
        double usdAmount = buyRequest.getUsdAmount();

        // Fetch user
        Person person = personRepository.findByEmail(email);
        if (person == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Fetch crypto price
        double cryptoPrice = cryptoService.getCryptoPrice(cryptoId);
        if (cryptoPrice <= 0) {
            return ResponseEntity.status(500).body("Failed to fetch live crypto price.");
        }

        // Calculate crypto amount
        double cryptoAmount = usdAmount / cryptoPrice;

        // Check for sufficient balance
        if (person.getBalanceDouble() < usdAmount) {
            return ResponseEntity.badRequest().body("Insufficient balance.");
        }

        // Deduct balance and update user's holdings
        double updatedBalance = person.getBalanceDouble() - usdAmount;
        person.setBalanceString(updatedBalance);

        userStocksTable userStocks = person.getUser_stocks();
        if (userStocks == null) {
            userStocks = new userStocksTable("", cryptoId + ":" + cryptoAmount, String.valueOf(updatedBalance), person.getEmail(), person);
        } else {
            String updatedCrypto = addOrUpdateCryptoHoldings(userStocks.getCrypto(), cryptoId, cryptoAmount);
            userStocks.setCrypto(updatedCrypto);
            userStocks.setBalance(String.valueOf(updatedBalance));
        }

        // Save to database
        userStocksRepo.save(userStocks);
        personRepository.save(person);

        return ResponseEntity.ok("Successfully purchased " + cryptoAmount + " of " + cryptoId + " for $" + usdAmount);
    }

    // Endpoint to allow users to sell cryptocurrency
    @PostMapping("/sell")
    public ResponseEntity<?> sellCrypto(@RequestBody SellRequest sellRequest) {
        String email = sellRequest.getEmail();
        String cryptoId = sellRequest.getCryptoId();
        double cryptoAmount = sellRequest.getCryptoAmount();

        // Fetch user
        Person person = personRepository.findByEmail(email);
        if (person == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Fetch crypto price
        double cryptoPrice = cryptoService.getCryptoPrice(cryptoId);
        if (cryptoPrice <= 0) {
            return ResponseEntity.status(500).body("Failed to fetch live crypto price.");
        }

        // Fetch and update crypto holdings
        userStocksTable userStocks = person.getUser_stocks();
        if (userStocks == null || userStocks.getCrypto() == null || userStocks.getCrypto().isEmpty()) {
            return ResponseEntity.badRequest().body("No crypto holdings found to sell.");
        }

        String updatedCrypto = removeOrUpdateCryptoHoldings(userStocks.getCrypto(), cryptoId, cryptoAmount);
        if (updatedCrypto == null) {
            return ResponseEntity.badRequest().body("Insufficient crypto to sell.");
        }

        // Update balance
        double totalValueSold = cryptoPrice * cryptoAmount;
        double updatedBalance = person.getBalanceDouble() + totalValueSold;
        person.setBalanceString(updatedBalance);
        userStocks.setCrypto(updatedCrypto);
        userStocks.setBalance(String.valueOf(updatedBalance));

        // Save to database
        userStocksRepo.save(userStocks);
        personRepository.save(person);

        return ResponseEntity.ok("Successfully sold " + cryptoAmount + " of " + cryptoId + " for $" + totalValueSold);
    }

    // Utility method to add or update crypto holdings
    private String addOrUpdateCryptoHoldings(String currentCrypto, String cryptoId, double cryptoAmount) {
        StringBuilder updatedCrypto = new StringBuilder();
        boolean updated = false;
    
        // Check if currentCrypto is empty or null
        if (currentCrypto != null && !currentCrypto.isEmpty()) {
            String[] holdings = currentCrypto.split(",");
            for (String holding : holdings) {
                if (holding.isEmpty()) continue; // Skip empty strings
    
                String[] parts = holding.split(":");
                if (parts.length != 2) continue; // Skip malformed entries
    
                String id = parts[0];
                double amount = Double.parseDouble(parts[1]);
    
                // Update the crypto amount if the ID matches
                if (id.equalsIgnoreCase(cryptoId)) {
                    amount += cryptoAmount;
                    updated = true;
                }
                updatedCrypto.append(id).append(":").append(amount).append(",");
            }
        }
    
        // If the crypto ID wasn't found, add it as a new entry
        if (!updated) {
            updatedCrypto.append(cryptoId).append(":").append(cryptoAmount).append(",");
        }
    
        // Remove trailing comma and return the updated string
        return updatedCrypto.toString().replaceAll(",$", "");
    }
    

    // Utility method to remove or update crypto holdings
    private String removeOrUpdateCryptoHoldings(String currentCrypto, String cryptoId, double cryptoAmount) {
        StringBuilder updatedCrypto = new StringBuilder();
        boolean removed = false;
    
        // Check if currentCrypto is null or empty
        if (currentCrypto != null && !currentCrypto.trim().isEmpty()) {
            String[] holdings = currentCrypto.split(",");
            for (String holding : holdings) {
                if (holding == null || holding.trim().isEmpty()) continue; // Skip empty entries
    
                String[] parts = holding.split(":");
                if (parts.length != 2) continue; // Skip malformed entries
    
                String id = parts[0].trim();
                double amount = Double.parseDouble(parts[1].trim());
    
                // Reduce the crypto amount if ID matches
                if (id.equalsIgnoreCase(cryptoId)) {
                    if (amount < cryptoAmount) {
                        throw new RuntimeException("Insufficient crypto balance to sell.");
                    }
                    amount -= cryptoAmount;
                    removed = true;
                }
    
                // Append only non-zero amounts
                if (amount > 0) {
                    updatedCrypto.append(id).append(":").append(amount).append(",");
                }
            }
        }
    
        if (!removed) {
            throw new RuntimeException("Crypto ID not found in user's holdings.");
        }
    
        // Remove the trailing comma if any
        return updatedCrypto.toString().replaceAll(",$", "");
    }
    

    // Inner DTO class for BuyRequest
    static class BuyRequest {
        private String email;
        private String cryptoId;
        private double usdAmount;

        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getCryptoId() { return cryptoId; }
        public void setCryptoId(String cryptoId) { this.cryptoId = cryptoId; }
        public double getUsdAmount() { return usdAmount; }
        public void setUsdAmount(double usdAmount) { this.usdAmount = usdAmount; }
    }

    // Inner DTO class for SellRequest
    static class SellRequest {
        private String email;
        private String cryptoId;
        private double cryptoAmount;

        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getCryptoId() { return cryptoId; }
        public void setCryptoId(String cryptoId) { this.cryptoId = cryptoId; }
        public double getCryptoAmount() { return cryptoAmount; }
        public void setCryptoAmount(double cryptoAmount) { this.cryptoAmount = cryptoAmount; }
    }
}
