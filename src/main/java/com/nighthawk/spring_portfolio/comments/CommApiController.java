package com.nighthawk.spring_portfolio.comments;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/Comments")
public class CommApiController {

    private final SubmisionCommentJPA CommentJPA;

    // Constructor injection for CommentJPA
    public CommApiController(SubmisionCommentJPA CommentJPA) {
        this.CommentJPA = CommentJPA;
    }

    /**
     * Endpoint to create a new comment
     * @param comment - The Comment object received as JSON
     * @return ResponseEntity with the saved Comment and HTTP status CREATED
     */
    @PostMapping("/create")
    public ResponseEntity<SubmisionComment> createComment(@RequestBody SubmisionComment comment) {
        SubmisionComment savedComment = CommentJPA.save(comment);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }

    /**
     * Endpoint to retrieve all comments
     * @return ResponseEntity with a list of all comments and HTTP status OK
     */
    @GetMapping("/all")
    public ResponseEntity<List<SubmisionComment>> getAllComments() {
        List<SubmisionComment> comments = CommentJPA.findAll();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve a comment by its ID
     * @param id - ID of the comment to retrieve
     * @return ResponseEntity with the comment if found, or NOT FOUND status
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubmisionComment> getCommentById(@PathVariable Long id) {
        return CommentJPA.findById(id)
                .map(comment -> new ResponseEntity<>(comment, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Endpoint to retrieve comments by assignment
     * @param assignment - The assignment name to filter comments by
     * @return ResponseEntity with the list of comments if found, or NOT FOUND status
     */
    @GetMapping("/by-assignment")
    public ResponseEntity<List<SubmisionComment>> getCommentsByAssignment(@RequestParam String assignment) {
        List<SubmisionComment> comments = CommentJPA.findByAssignment(assignment);
        
        if (comments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Return 404 if no comments found
        }
        
        return new ResponseEntity<>(comments, HttpStatus.OK);  // Return 200 with the list of comments
    }

    /**
     * Endpoint to retrieve comments by author
     * @param author - The author's name to filter comments by
     * @return ResponseEntity with the list of comments if found, or NOT FOUND status
     */
    @GetMapping("/by-author")
    public ResponseEntity<List<SubmisionComment>> getCommentsByAuthor(@RequestParam String author) {
        List<SubmisionComment> comments = CommentJPA.findByAuthor(author);
        
        if (comments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Return 404 if no comments found
        }
        
        return new ResponseEntity<>(comments, HttpStatus.OK);  // Return 200 with the list of comments
    }
}
