package com.nighthawk.spring_portfolio.mvc.issue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/issue")
public class IssueApiController {
    @Autowired
    private IssueJPARepository repository;

    // @PostMapping("/add/{issueName}")
    // public ResponseEntity<Object> addToIssue(@PathVariable String issueName)
    // {
    //     Issue issue = new Issue(issueName, 0);
    //     repository.save(issue);
    //     return new ResponseEntity<>(issue, HttpStatus.OK);        
    // }
    @Getter
    public static class IssueDto
{
    private String bathroomName;
    private int count;
    private String issue;
}
    @PostMapping("/add")
    public ResponseEntity<Object> addIssue(@RequestBody IssueDto issueDto)
    {
        Issue issue = new Issue(issueDto.getBathroomName(), issueDto.getIssue(), issueDto.getCount());
        repository.save(issue);
        return new ResponseEntity<>("Issue at: " + issueDto.getBathroomName() + " for " + issueDto.getIssue() + "has been successfully created", HttpStatus.CREATED);
    }

    @Getter
    public static class UpdateDto
    {
        private String bathroomName;
        private String issue;
    }

    @CrossOrigin(origins = "http://127.0.0.1:4100")
    @PutMapping("/update")
    public ResponseEntity<Object> updateIssue(@RequestBody UpdateDto updateDto)
    {
        Optional<Issue> optionalIssue = repository.findByIssueAndBathroom(updateDto.getIssue(), updateDto.getBathroomName());
        if(optionalIssue.isPresent())
        {
            Issue report = optionalIssue.get();

            report.setCount(report.getCount() + 1);
            repository.save(report);
            return new ResponseEntity<>(report, HttpStatus.OK);
        }
        return new ResponseEntity<>("Issue not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/issues")
    
        public ResponseEntity<List<Issue>> getCurrentIssues()
        {
            return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
        }
    
}