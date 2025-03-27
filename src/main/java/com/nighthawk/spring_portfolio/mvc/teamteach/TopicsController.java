package com.nighthawk.spring_portfolio.mvc.teamteach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicsController {

    @Autowired
    private TopicRepository topicRepository;

    @PostMapping("/add")
    public Topic addTopic(@RequestBody Topic topic) {
        return topicRepository.save(topic);
    }

    @GetMapping("/all")
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    @PutMapping("/{topicId}/signup")
    public Topic signUpStudent(@PathVariable Long topicId, @RequestBody Student student) {
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new RuntimeException("Topic not found"));
        topic.getStudents().add(student);
        return topicRepository.save(topic);
    }
}
