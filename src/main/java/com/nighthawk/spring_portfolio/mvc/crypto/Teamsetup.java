// package com.nighthawk.spring_portfolio.mvc.team;

// import java.util.HashMap;
// import java.util.Map;
// import java.util.List;
// import java.util.ArrayList;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RestController;

// import com.nighthawk.spring_portfolio.mvc.person.Person;
// import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;

// /**
//  * This class provides API endpoints for managing team creation, joining, and capacity.
//  */
// @RestController
// @CrossOrigin(origins = {"*"})
// public class Teamsetup {

//     @Autowired
//     private PersonJpaRepository repository;

//     // Cache to store users and their balances
//     private Map<String, Double> userBalancesCache = new HashMap<>();
//     private long lastCacheUpdateTime = 0;  // Track the last cache update time
//     private final long CACHE_EXPIRY_TIME = 600000;  // 10 minutes in milliseconds

//     // Represents available teams and their capacities
//     private Map<String, Integer> teams = new HashMap<>();
//     private final int TEAM_CAPACITY = 5;  // Max number of members per team

//     /**
//      * Add a person to a team. If the team is full, return a message.
//      * @param teamName Name of the team.
//      * @param authentication The authentication object for the current user.
//      * @return A ResponseEntity indicating whether the person was added or the team is full.
//      */
//     @PostMapping("/team/join/{teamName}")
//     public ResponseEntity<String> joinTeam(@PathVariable String teamName, Authentication authentication) {
//         // Check if team exists in the system; if not, create it with 0 members
//         teams.putIfAbsent(teamName, 0);

//         // Check if the team is full
//         if (teams.get(teamName) >= TEAM_CAPACITY) {
//             return new ResponseEntity<>("This team is full.", HttpStatus.BAD_REQUEST);
//         }

//         // Increment the team size
//         teams.put(teamName, teams.get(teamName) + 1);

//         // Get the current authenticated person
//         UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//         String email = userDetails.getUsername(); // Assuming email is used as username

//         Optional<Person> personOptional = repository.findByUid(email);
//         if (personOptional.isPresent()) {
//             Person person = personOptional.get();
//             // Add the person to the team (this is just an example, you would need to save this info somewhere)
//             person.setTeam(teamName);
//             repository.save(person);
//             // Update the cache for the balance
//             updateUserBalanceCache();
//             return new ResponseEntity<>("You have successfully joined the " + teamName + " team.", HttpStatus.OK);
//         }

//         return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
//     }

//     /**
//      * Get the current number of members in a team.
//      * @param teamName Name of the team.
//      * @return A ResponseEntity with the number of members in the team.
//      */
//     @GetMapping("/team/members/{teamName}")
//     public ResponseEntity<String> getTeamMembers(@PathVariable String teamName) {
//         // Check if the team exists and return the current number of members
//         Integer teamSize = teams.get(teamName);
//         if (teamSize == null) {
//             return new ResponseEntity<>("Team does not exist.", HttpStatus.NOT_FOUND);
//         }
//         return new ResponseEntity<>("Current team size: " + teamSize, HttpStatus.OK);
//     }

//     /**
//      * Create a new team if it doesn't already exist.
//      * @param teamName Name of the new team.
//      * @return A ResponseEntity indicating whether the team was created or already exists.
//      */
//     @PostMapping("/team/create/{teamName}")
//     public ResponseEntity<String> createTeam(@PathVariable String teamName) {
//         // Check if the team already exists
//         if (teams.containsKey(teamName)) {
//             return new ResponseEntity<>("Team already exists.", HttpStatus.BAD_REQUEST);
//         }

//         // Create the new team with 0 members
//         teams.put(teamName, 0);
//         return new ResponseEntity<>("Team " + teamName + " created successfully.", HttpStatus.OK);
//     }

//     /**
//      * Get a list of all users and their current balance from the cached data.
//      * @return A ResponseEntity with the list of users and balances.
//      */
//     @GetMapping("/users/balances")
//     public ResponseEntity<List<String>> getUsersBalances() {
//         // If the cache is expired or is empty, update it
//         if (System.currentTimeMillis() - lastCacheUpdateTime > CACHE_EXPIRY_TIME || userBalancesCache.isEmpty()) {
//             updateUserBalanceCache();
//         }
        
//         List<String> userBalances = new ArrayList<>();
        
//         // Add the cached balances to the response
//         for (Map.Entry<String, Double> entry : userBalancesCache.entrySet()) {
//             userBalances.add("User: " + entry.getKey() + ", Balance: " + entry.getValue());
//         }
        
//         return new ResponseEntity<>(userBalances, HttpStatus.OK);
//     }

//     /**
//      * Update the user balances cache with the current data from the database.
//      */
//     private void updateUserBalanceCache() {
//         List<Person> people = repository.findAll();
//         userBalancesCache.clear();  // Clear the existing cache
        
//         for (Person person : people) {
//             userBalancesCache.put(person.getUid(), person.getBalance());
//         }

//         lastCacheUpdateTime = System.currentTimeMillis();  // Update the cache timestamp
//     }
// }