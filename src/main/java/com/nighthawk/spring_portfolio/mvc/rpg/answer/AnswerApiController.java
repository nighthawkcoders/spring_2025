package com.nighthawk.spring_portfolio.mvc.rpg.answer;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;
import com.nighthawk.spring_portfolio.mvc.rpg.question.Question;
import com.nighthawk.spring_portfolio.mvc.rpg.question.QuestionJpaRepository;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
@RestController
@RequestMapping("/rpg_answer")
@CrossOrigin(origins = "http://127.0.0.1:5501") 
public class AnswerApiController {

    // Load environment variables using dotenv
    private final Dotenv dotenv = Dotenv.load();
    private final String apiUrl = dotenv.get("GAMIFY_API_URL");
    private final String apiKey = dotenv.get("GAMIFY_API_KEY");

    @Autowired
    private AnswerJpaRepository answerJpaRepository;
    @Autowired
    private PersonJpaRepository personJpaRepository;
    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @Getter 
    public static class AnswerDto {
        private String content;
        private Long questionId;
        private Long personId;
        private Long chatScore; 
    }

    @GetMapping("/getQuestionsAnswered/{personid}")
    public ResponseEntity<Integer> getQuestionsAnswered(@PathVariable Integer personid) {
        List<Answer> useranswers = answerJpaRepository.findByPersonId(personid);

        Integer questionsAnswered = useranswers.size();

        return new ResponseEntity<>(questionsAnswered, HttpStatus.OK);

    }

    @GetMapping("getQuestions")
    public ResponseEntity<List<Question>> getQuestions() {
        List<Question> questions = questionJpaRepository.findAllByOrderByTitleAsc();

        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    @GetMapping("getQuestion/{questionid}") 
    public ResponseEntity<Question> getQuestion(@PathVariable Integer questionid) {
        Question question = questionJpaRepository.findById(questionid);

        return new ResponseEntity<>(question, HttpStatus.OK);
    }

    @GetMapping("/getChatScore/{personid}")
    public ResponseEntity<Long> getChatScore(@PathVariable Integer personid) {
        List<Answer> personsanswers = answerJpaRepository.findByPersonId(personid);
        Long totalChatScore = 0L;

        for (Answer personanswer : personsanswers) {
            Long questionChatScore = personanswer.getChatScore();
            totalChatScore += questionChatScore;
        }

        return new ResponseEntity<>(totalChatScore, HttpStatus.OK);

    }

    @GetMapping("/getBalance/{personid}") 
    public ResponseEntity<Double> getBalance(@PathVariable Long personid) {
        Optional<Person> optional = personJpaRepository.findById(personid);
        Person personOpt = optional.get();  
        

        Double balance = personOpt.getBalance();

        return new ResponseEntity<>(balance, HttpStatus.OK);

    }

    @PostMapping("/submitAnswer")
    public ResponseEntity<Answer> postAnswer(@RequestBody AnswerDto answerDto) {
        Optional<Question> questionOpt = questionJpaRepository.findById(answerDto.getQuestionId());
        Optional<Person> personOpt = personJpaRepository.findById(answerDto.getPersonId());
        
        System.out.println("API Key: " + apiKey);

        if (questionOpt.isEmpty() || personOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Question question = questionOpt.get();
        Person person = personOpt.get();

        // System.out.println(person);
        System.out.println(question);

        String rubric = "Correctness and Completeness (500 points): 500 - completely correct, "
                        + "450 - minor issues or unhandled edge cases, 400 - several small errors, "
                        + "350 - partial with multiple issues, below 300 - major issues/incomplete; "
                        + "Efficiency and Optimization (200 points): 200 - optimal or near-optimal, "
                        + "180 - minor optimization needed, 160 - functional but inefficient, "
                        + "140 - improvements needed, below 140 - inefficient; Code Structure and Organization "
                        + "(150 points): 150 - well-organized, 130 - mostly organized, 110 - readable but lacks structure, "
                        + "90 - hard to follow, below 90 - unorganized; Readability and Documentation (100 points): "
                        + "100 - clear, well-documented, 85 - readable but limited comments, 70 - somewhat readable, "
                        + "50 - minimally readable, below 50 - poor readability; Error Handling and Edge Cases "
                        + "(50 points): 50 - handles all cases, 40 - most cases covered, 30 - some cases covered, "
                        + "20 - minimal handling, below 20 - little attention; Extra Credit (100 points): "
                        + "impressive/innovative elements. Give me an integer score from 1-1000 AND ONLY RESPOND WITH A NUMBER AND NO TEXT.";

        Long chatScore = getChatScore(answerDto.getContent(), rubric);

        Answer answer = new Answer(answerDto.getContent(), question, person, chatScore);
        answerJpaRepository.save(answer);

        // updateBalance
        double questionPoints = question.getPoints();
        person.setBalance(person.getBalance() + questionPoints);
        personJpaRepository.save(person);


        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    private Long getChatScore(String content, String rubric) {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        // Construct JSON request body
        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("model", "gpt-3.5-turbo");

        // Construct messages array
        ArrayNode messages = requestBody.putArray("messages");
        ObjectNode systemMessage = messages.addObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", rubric);

        ObjectNode userMessage = messages.addObject();
        userMessage.put("role", "user");
        userMessage.put("content", content);

        requestBody.put("temperature", 0.0);

        // Create request
        okhttp3.RequestBody body = okhttp3.RequestBody.create(requestBody.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                System.out.println(response);
                JsonNode jsonNode = mapper.readTree(response.body().string());
                String chatGptResponse = jsonNode.get("choices").get(0).get("message").get("content").asText();
                return Long.parseLong(chatGptResponse);
            } else {
                System.err.println("Request failed: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0L;
    }


    /* 
    @GetMapping("/leaderboard")
    public List<LeaderboardDto> getLeaderboard() {
    List<LeaderboardDto> leaderboardEntries = answerJpaRepository.findTop10UsersByTotalScore();

    for (LeaderboardDto entry : leaderboardEntries) {
        Optional<User> user = userJpaRepository.findById(entry.getId());
        String userName = user.isPresent() ? user.get().getUsername() : "Unknown";
        entry.setuserName(userName);
    }

    return leaderboardEntries;
    }  
    */
}
    

    
    
 

