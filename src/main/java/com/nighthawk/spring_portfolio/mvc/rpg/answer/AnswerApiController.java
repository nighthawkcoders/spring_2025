// define package and import necessary libraries
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

// define controller for handling answer-related api requests
@RestController
@RequestMapping("/rpg_answer") // set base url for controller
@CrossOrigin(origins = "http://127.0.0.1:5501") // allow cross-origin requests
public class AnswerApiController {

    // initialize environment variables using dotenv library
    private final Dotenv dotenv = Dotenv.load();
    private final String apiUrl = dotenv.get("GAMIFY_API_URL"); // fetch api url from environment variables
    private final String apiKey = dotenv.get("GAMIFY_API_KEY"); // fetch api key from environment variables

    // autowire repositories for database interactions
    @Autowired
    private AnswerJpaRepository answerJpaRepository;
    @Autowired
    private PersonJpaRepository personJpaRepository;
    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    // define dto class for handling answer data
    @Getter 
    public static class AnswerDto {
        private String content; // store answer content
        private Long questionId; // store related question id
        private Long personId; // store related person id
        private Long chatScore; // store chat score
    }

    // api endpoint to get count of questions answered by a person
    @GetMapping("/getQuestionsAnswered/{personid}")
    public ResponseEntity<Integer> getQuestionsAnswered(@PathVariable Integer personid) {
        List<Answer> useranswers = answerJpaRepository.findByPersonId(personid); // fetch answers by person id

        Integer questionsAnswered = useranswers.size(); // count total answers

        return new ResponseEntity<>(questionsAnswered, HttpStatus.OK); // return count with http ok status
    }

    // api endpoint to fetch all questions
    @GetMapping("getQuestions")
    public ResponseEntity<List<Question>> getQuestions() {
        List<Question> questions = questionJpaRepository.findAllByOrderByTitleAsc(); // fetch questions ordered by title

        return new ResponseEntity<>(questions, HttpStatus.OK); // return question list with http ok status
    }

    // api endpoint to fetch a single question by id
    @GetMapping("getQuestion/{questionid}") 
    public ResponseEntity<Question> getQuestion(@PathVariable Integer questionid) {
        Question question = questionJpaRepository.findById(questionid); // fetch question by id

        return new ResponseEntity<>(question, HttpStatus.OK); // return question with http ok status
    }

    // api endpoint to get total chat score of a person
    @GetMapping("/getChatScore/{personid}")
    public ResponseEntity<Long> getChatScore(@PathVariable Integer personid) {
        List<Answer> personsanswers = answerJpaRepository.findByPersonId(personid); // fetch answers by person id
        Long totalChatScore = 0L; // initialize total chat score

        // loop through answers to calculate total chat score
        for (Answer personanswer : personsanswers) {
            Long questionChatScore = personanswer.getChatScore();
            totalChatScore += questionChatScore; // add chat score to total
        }

        return new ResponseEntity<>(totalChatScore, HttpStatus.OK); // return total chat score with http ok status
    }

    // api endpoint to fetch balance of a person by id
    @GetMapping("/getBalance/{personid}") 
    public ResponseEntity<Double> getBalance(@PathVariable Long personid) {
        Optional<Person> optional = personJpaRepository.findById(personid); // fetch person by id
        Person personOpt = optional.get(); // get person object from optional

        Double balance = personOpt.getBalance(); // fetch balance

        return new ResponseEntity<>(balance, HttpStatus.OK); // return balance with http ok status
    }

    // api endpoint to submit an answer
    @PostMapping("/submitAnswer")
    public ResponseEntity<Answer> postAnswer(@RequestBody AnswerDto answerDto) {
        Optional<Question> questionOpt = questionJpaRepository.findById(answerDto.getQuestionId()); // fetch question by id
        Optional<Person> personOpt = personJpaRepository.findById(answerDto.getPersonId()); // fetch person by id
        
        // print api key for debugging
        System.out.println("api key: " + apiKey);

        // check if question or person does not exist
        if (questionOpt.isEmpty() || personOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // return not found status
        }

        Question question = questionOpt.get(); // get question object
        Person person = personOpt.get(); // get person object

        System.out.println(question); // print question for debugging

        // define rubric for chat score calculation
        String rubric = "correctness and completeness (500 points): 500 - completely correct, "
                        + "450 - minor issues or unhandled edge cases, 400 - several small errors, "
                        + "350 - partial with multiple issues, below 300 - major issues/incomplete; "
                        + "efficiency and optimization (200 points): 200 - optimal or near-optimal, "
                        + "180 - minor optim needed, 160 - functional but inefficient, "
                        + "140 - improvements needed, below 140 - inefficient; code structure and organization "
                        + "(150 points): 150 - well-organized, 130 - mostly organized, 110 - readable but lacks structure, "
                        + "90 - hard to follow, below 90 - unorganized; readability and documentation (100 points): "
                        + "100 - clear, well-documented, 85 - readable but limited comments, 70 - somewhat readable, "
                        + "50 - minimally readable, below 50 - poor readability; error handling and edge cases "
                        + "(50 points): 50 - handles all cases, 40 - most cases covered, 30 - some cases covered, "
                        + "20 - minimal handling, below 20 - little attention; extra credit (100 points): "
                        + "impressive/innovative elements. give me an integer score from 1-1000 and only respond with a number and no text.";

        Long chatScore = getChatScore(answerDto.getContent(), rubric); // calculate chat score based on rubric

        Answer answer = new Answer(answerDto.getContent(), question, person, chatScore); // create new answer object
        answerJpaRepository.save(answer); // save answer to database

        // update balance of person based on question points
        double questionPoints = question.getPoints();
        person.setBalance(person.getBalance() + questionPoints);
        personJpaRepository.save(person); // save updated person object

        return new ResponseEntity<>(answer, HttpStatus.OK); // return saved answer with http ok status
    }

    // method to calculate chat score using external api
    private Long getChatScore(String content, String rubric) {
        OkHttpClient client = new OkHttpClient(); // initialize http client
        ObjectMapper mapper = new ObjectMapper(); // initialize object mapper

        // construct json request body
        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("model", "gpt-3.5-turbo"); // specify model

        // construct messages array
        ArrayNode messages = requestBody.putArray("messages");
        ObjectNode systemMessage = messages.addObject();
        systemMessage.put("role", "system"); // define system role
        systemMessage.put("content", rubric); // add rubric content

        ObjectNode userMessage = messages.addObject();
        userMessage.put("role", "user"); // define user role
        userMessage.put("content", content); // add user content

        requestBody.put("temperature", 0.0); // set temperature for model response

        // create http request
        okhttp3.RequestBody body = okhttp3.RequestBody.create(requestBody.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("authorization", "bearer " + apiKey) // add api key authorization
                .build();

        try (Response response = client.newCall(request).execute()) { // execute http request
            if (response.isSuccessful() && response.body() != null) {
                System.out.println(response); // print response for debugging
                JsonNode jsonNode = mapper.readTree(response.body().string()); // parse response
                String chatGptResponse = jsonNode.get("choices").get(0).get("message").get("content").asText(); // extract score
                return Long.parseLong(chatGptResponse); // return score as long
            } else {
                System.err.println("request failed: " + response); // print error if request fails
            }
        } catch (IOException e) {
            e.printStackTrace(); // print stack trace if exception occurs
        }
        return 0L; // return 0 if failure
    }

    /* 
    // commented out leaderboard method for future implementation
    @GetMapping("/leaderboard")
    public List<LeaderboardDto> getLeaderboard() {
        List<LeaderboardDto> leaderboardEntries = answerJpaRepository.findTop10UsersByTotalScore();

        for (LeaderboardDto entry : leaderboardEntries) {
            Optional<User> user = userJpaRepository.findById(entry.getId());
            String userName = user.isPresent() ? user.get().getUsername() : "unknown";
            entry.setuserName(userName);
        }

        return leaderboardEntries;
    }  
    */
}