package com.nighthawk.spring_portfolio.mvc.seedtracker;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://127.0.0.1:4100")
@RestController
@RequestMapping("/api/seeds")
public class SeedApiController {

    @Autowired
    private SeedJpaRepository seedJpaRepository;

    // Add a new seed entry
    @PostMapping("/")
    public Seed addSeed(@RequestBody Seed seed) {
        if (seed.getStudentId() == null) {
            Long maxStudentId = seedJpaRepository.findMaxStudentId().orElse(0L);
            seed.setStudentId(maxStudentId + 1);
        }
        return seedJpaRepository.save(seed);
    }

    // Retrieve seeds by studentId
    @GetMapping("/{studentId}")
    public List<Seed> getSeedsByStudentId(@PathVariable Long studentId) {
        return seedJpaRepository.findByStudentId(studentId);
    }

    // Update a seed entry by id
    @PutMapping("/{id}")
    public Seed updateSeed(@PathVariable Long id, @RequestBody Seed updatedSeed) {
        System.out.println("Received request to update seed with id: " + id);
        System.out.println("Updated grade: " + updatedSeed.getGrade());

        return seedJpaRepository.findById(id)
                .map(seed -> {
                    if (updatedSeed.getGrade() != null) {
                        seed.setGrade(updatedSeed.getGrade());
                    }
                    System.out.println("Saving updated seed: " + seed);
                    return seedJpaRepository.save(seed);
                })
                .orElseThrow(() -> {
                    System.out.println("Seed not found with id " + id);
                    return new RuntimeException("Seed not found with id " + id);
                });
    }

    // Delete a seed entry by id
    @DeleteMapping("/{id}")
    public void deleteSeed(@PathVariable Long id) {
        seedJpaRepository.deleteById(id);
    }

    @GetMapping("/")
    public List<Seed> getAllSeeds() {
        return seedJpaRepository.findAll();
    }
}
