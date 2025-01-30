package com.nighthawk.spring_portfolio.mvc.backups;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nighthawk.spring_portfolio.mvc.assignments.Assignment;
import com.nighthawk.spring_portfolio.mvc.assignments.AssignmentJpaRepository;
import com.nighthawk.spring_portfolio.mvc.assignments.AssignmentQueue;
import com.nighthawk.spring_portfolio.mvc.assignments.AssignmentSubmission;
import com.nighthawk.spring_portfolio.mvc.assignments.AssignmentSubmissionJPA;
import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/imports")
public class ImportsController {

    @Autowired
    private PersonJpaRepository personRepo;

    @Autowired
    private AssignmentJpaRepository assignmentRepo;

    @Autowired
    private AssignmentSubmissionJPA submissionRepo;

    private final ObjectMapper objectMapper = new ObjectMapper(); // For JSON deserialization

    /**
     * Import persons from a CSV file.
     */
    @PostMapping("/persons")
    public ResponseEntity<String> importPersons(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // Skip the header row
                    continue;
                }

                String[] data = line.split(",");
                if (data.length < 11) {
                    continue; // Skip invalid rows
                }

                Person person = new Person();
                person.setId(Long.parseLong(data[0]));
                person.setBalance(data[1]);
                person.setDob(parseDate(data[2]));
                person.setEmail(data[3]);
                person.setKasmServerNeeded(Boolean.parseBoolean(data[4]));
                person.setName(data[5]);
                person.setPassword(data[6]);
                person.setPfp(data[7]);
                person.setSid(data[8]);

                person.setGrades(new ArrayList<>());  
                person.setSubmissions(new ArrayList<>());  

                // Deserialize the stats JSON string into a Map<String, Map<String, Object>>
                if (!data[9].isEmpty()) {
                    Map<String, Map<String, Object>> statsMap = objectMapper.readValue(
                        data[9], new TypeReference<Map<String, Map<String, Object>>>() {}
                    );
                    person.setStats(statsMap);
                } else {
                    person.setStats(null); // Set stats to null if the field is empty
                }

                person.setUid(data[10]);

                // Save the person
                personRepo.save(person);
            }

            return ResponseEntity.ok("Persons imported successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to import persons: " + e.getMessage());
        }
    }

    /**
     * Import assignments from a CSV file.
     */
    // @PostMapping("/assignments")
    // public ResponseEntity<String> importAssignments(@RequestParam("file") MultipartFile file) {
    //     try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
    //         String line;
    //         boolean isHeader = true;

    //         while ((line = reader.readLine()) != null) {
    //             if (isHeader) {
    //                 isHeader = false; // Skip the header row
    //                 continue;
    //             }

    //             String[] data = line.split(",");
    //             if (data.length < 9) {
    //                 continue; // Skip invalid rows
    //             } 

    //             Assignment assignment = new Assignment();
    //             assignment.setId(Long.parseLong(data[0]));

    //             // Deserialize the assignment_queue JSON string into an AssignmentQueue object
    //             if (!data[1].isEmpty()) {
    //                 AssignmentQueue assignmentQueue = objectMapper.readValue(data[1], AssignmentQueue.class);
    //                 assignment.setAssignmentQueue(assignmentQueue);
    //             } else {
    //                 assignment.setAssignmentQueue(null); // Set assignmentQueue to null if the field is empty
    //             }

    //             assignment.setDescription(data[2]);
    //             assignment.setDueDate(data[3]);
    //             assignment.setName(data[4]);
    //             assignment.setPoints(Double.parseDouble(data[5]));
    //             assignment.setPresentationLength(data[6].isEmpty() ? null : Long.parseLong(data[6])); // Handle null
    //             assignment.setTimestamp(data[7]);
    //             assignment.setType(data[8]);

    //             assignmentRepo.save(assignment);
    //         }

    //         return ResponseEntity.ok("Assignments imported successfully");
    //     } catch (IOException e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to import assignments: " + e.getMessage());
    //     }
    // }

    /**
     * Import submissions from a CSV file.
     */
    @PostMapping("/submissions")
    public ResponseEntity<String> importSubmissions(@RequestParam("file") MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // Skip the header row
                    continue;
                }

                String[] data = line.split(",");
                if (data.length < 8) {
                    continue; // Skip invalid rows
                }

                AssignmentSubmission submission = new AssignmentSubmission();
                submission.setId(Long.parseLong(data[0]));
                submission.setAssignment(assignmentRepo.findById(Long.parseLong(data[1])).orElse(null));
                submission.setComment(data[2]);
                submission.setContent(data[3]);
                submission.setFeedback(data[4].isEmpty() ? null : data[4]); // Handle null feedback
                submission.setGrade(data[5].isEmpty() ? null : Double.parseDouble(data[5])); // Handle null grade
                submission.setStudent(personRepo.findById(Long.parseLong(data[7])).orElse(null));

                submissionRepo.save(submission);
            }

            return ResponseEntity.ok("Submissions imported successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to import submissions: " + e.getMessage());
        }
    }

    /**
     * Helper method to parse a date string into a Date object.
     */
    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }
}