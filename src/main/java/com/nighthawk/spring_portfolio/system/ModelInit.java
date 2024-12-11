package com.nighthawk.spring_portfolio.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nighthawk.spring_portfolio.mvc.announcement.Announcement;
import com.nighthawk.spring_portfolio.mvc.announcement.AnnouncementJPA;
import com.nighthawk.spring_portfolio.mvc.bathroom.BathroomQueue;
import com.nighthawk.spring_portfolio.mvc.bathroom.BathroomQueueJPARepository;
import com.nighthawk.spring_portfolio.mvc.bathroom.Teacher;
import com.nighthawk.spring_portfolio.mvc.bathroom.TeacherJpaRepository;
import com.nighthawk.spring_portfolio.mvc.bathroom.Tinkle;
import com.nighthawk.spring_portfolio.mvc.bathroom.TinkleJPARepository;
import com.nighthawk.spring_portfolio.mvc.comment.Comment;
import com.nighthawk.spring_portfolio.mvc.comment.CommentJPA;
import com.nighthawk.spring_portfolio.mvc.jokes.Jokes;
import com.nighthawk.spring_portfolio.mvc.jokes.JokesJpaRepository;
import com.nighthawk.spring_portfolio.mvc.mortevision.Assignment;
import com.nighthawk.spring_portfolio.mvc.mortevision.AssignmentJpaRepository;
import com.nighthawk.spring_portfolio.mvc.note.Note;
import com.nighthawk.spring_portfolio.mvc.note.NoteJpaRepository;
import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonDetailsService;
import com.nighthawk.spring_portfolio.mvc.person.PersonRole;
import com.nighthawk.spring_portfolio.mvc.person.PersonRoleJpaRepository;
@Component
@Configuration // Scans Application for ModelInit Bean, this detects CommandLineRunner
public class ModelInit {  
    @Autowired JokesJpaRepository jokesRepo;
    @Autowired NoteJpaRepository noteRepo;
    @Autowired PersonRoleJpaRepository roleJpaRepository;
    @Autowired PersonDetailsService personDetailsService;
    @Autowired AnnouncementJPA announcementJPA;
    @Autowired AssignmentJpaRepository assignmentJpa;
    @Autowired CommentJPA CommentJPA;
    @Autowired TinkleJPARepository tinkleJPA;
    @Autowired BathroomQueueJPARepository queueJPA;
    @Autowired TeacherJpaRepository teacherJPARepository;

    // @Autowired IssueJPARepository issueJPARepository;

    @Bean
    @Transactional
    CommandLineRunner run() {  
        return args -> {
            
            List<Announcement> announcements = Announcement.init();
            for (Announcement announcement : announcements) {
                Announcement announcementFound = announcementJPA.findByAuthor(announcement.getAuthor());  
                if (announcementFound == null) {
                    announcementJPA.save(new Announcement(announcement.getAuthor(), announcement.getTitle(), announcement.getBody(), announcement.getTags())); // JPA save
                }
            }

            List<Comment> Comments = Comment.init();
            for (Comment Comment : Comments) {
                List<Comment> CommentFound = CommentJPA.findByAssignment(Comment.getAssignment()); 
                if (CommentFound.isEmpty()) {
                    CommentJPA.save(new Comment(Comment.getAssignment(), Comment.getAuthor(), Comment.getText())); // JPA save
                }
            }

            Assignment[] assignments = Assignment.init();
            for (Assignment assignment : assignments) {
                Assignment assignmentFound = assignmentJpa.findByName(assignment.getName());  
                if (assignmentFound == null) {
                    assignmentJpa.save(new Assignment(assignment.getAssignmentId(), assignment.getName(), assignment.getStartDate(), assignment.getDueDate(), assignment.getRubric(), assignment.getPoints(), null)); // JPA save
                }
            }

            String[] jokesArray = Jokes.init();
            for (String joke : jokesArray) {
                List<Jokes> jokeFound = jokesRepo.findByJokeIgnoreCase(joke);  // JPA lookup
                if (jokeFound.size() == 0) {
                    jokesRepo.save(new Jokes(null, joke, 0, 0)); // JPA save
                }
            }

            Person[] personArray = Person.init();
            for (Person person : personArray) {
                List<Person> personFound = personDetailsService.list(person.getName(), person.getEmail());  // lookup
                if (personFound.size() == 0) { 
                    List<PersonRole> updatedRoles = new ArrayList<>();
                    for (PersonRole role : person.getRoles()) {
                        PersonRole roleFound = roleJpaRepository.findByName(role.getName());  // JPA lookup
                        if (roleFound == null) {
                            roleJpaRepository.save(role);  // JPA save
                            roleFound = role;
                        }
                        updatedRoles.add(roleFound);
                    }
                    // Update person with roles from role databasea
                    person.setRoles(updatedRoles); // Object reference is updated

                    personDetailsService.save(person); // JPA save

                    String text = "Test " + person.getEmail();
                    Note n = new Note(text, person);  
                    noteRepo.save(n);  // JPA Save                  
                }
            }

            Tinkle[] tinkleArray = Tinkle.init(personArray);
            for(Tinkle tinkle: tinkleArray)
            {
                // List<Tinkle> tinkleFound = 
                Optional<Tinkle> tinkleFound = tinkleJPA.findByPersonName(tinkle.getPerson_name());
                if(tinkleFound.isEmpty())
                {
                    tinkleJPA.save(tinkle);
                }
            }

            BathroomQueue[] queueArray = BathroomQueue.init();
            for(BathroomQueue queue: queueArray)
            {
                // List<Tinkle> tinkleFound = 
                Optional<BathroomQueue> queueFound = queueJPA.findByTeacherEmail(queue.getTeacherEmail());
                if(queueFound.isEmpty())
                {
                    queueJPA.save(queue);
                }
            }

            // Teacher API is populated with starting announcements
            List<Teacher> teachers = Teacher.init();
            for (Teacher teacher : teachers) {
                List<Teacher> existTeachers = teacherJPARepository.findByFirstnameIgnoreCaseAndLastnameIgnoreCase(teacher.getFirstname(), teacher.getLastname());
                if(existTeachers.isEmpty())
                teacherJPARepository.save(teacher); // JPA save
            }
            // Issue database initialization
            // Issue[] issueArray = Issue.init();
            // for (Issue issue : issueArray) {
            //     List<Issue> issueFound = issueJPARepository.findByIssueAndBathroomIgnoreCase(issue.getIssue(), issue.getBathroom());
            //     if (issueFound.isEmpty()) {
            //         issueJPARepository.save(issue);
            //     }
            // }
            // ArrayList<Tinkle> tinkles = new ArrayList<>();
            // for(Person person: personArray)
            // {
            //     tinkles.add(new Tinkle(person, "2"));
            // }
            // for(Tinkle tinkle: tinkles)
            // {
            //     tinkleJPA.save(tinkle);
            // }

        };
    }
}

