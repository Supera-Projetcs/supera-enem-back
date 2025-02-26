package com.supera.enem;
import com.supera.enem.domain.*;
import com.supera.enem.domain.enums.TestType;
import com.supera.enem.domain.enums.Weekday;
import com.supera.enem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class TestDataUtil {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private TestEntityRepository testEntityRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private WeeklyReportRepository weeklyReportRepository;

    /**
     * Cria e salva um Student no banco de dados.
     */
    public Student createAndSaveStudent() {
        Student student = new Student();
        student.setKeycloakId("keycloakUserId");
        student.setUsername("testuser");
        student.setFirstName("Test");
        student.setLastName("User");
        student.setDreamCourse("Computer Science");
        student.setPhone("123456789");
        student.setEmail("test@example.com");
        student.setBirthDate(LocalDate.of(1990, 1, 1));
        student.setPreferredStudyDays(Set.of(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.FRIDAY));
        return studentRepository.save(student);
    }

    /**
     * Cria e salva um Subject no banco de dados.
     */
    public Subject createAndSaveSubject() {
        Subject subject = new Subject();
        subject.setName("Test Subject");
//        return subjectRepository.save(subject);
        return subject;
    }

    /**
     * Cria e salva um Content no banco de dados.
     */
    public Content createAndSaveContent(Subject subject) {
        Content content = new Content();
        content.setName("Test Content");
        content.setContent_weight(0.5);
        content.setQuestion_weight(0.5);
        content.setSubject(subject);
        return contentRepository.save(content);
    }

    /**
     * Cria e salva uma Question no banco de dados.
     */
    public Question createAndSaveQuestion(Content content) {
        Question question = new Question();
        question.setText("What is 2 + 2?");
        question.setAnswer('A');
        question.setContents(Set.of(content));
        return questionRepository.save(question);
    }

    /**
     * Cria e salva uma Answer no banco de dados.
     */
    public Answer createAndSaveAnswer(Question question, TestEntity testEntity) {
        Answer answer = new Answer();
        answer.setText('A');
        answer.setCorrect(true);
        answer.setQuestion(question);
        answer.setTestEntity(testEntity);
        return answerRepository.save(answer);
    }

    /**
     * Cria e salva um TestEntity no banco de dados.
     */
    public TestEntity createAndSaveTestEntity(Student student, List<Question> questions) {
        TestEntity testEntity = new TestEntity();
        testEntity.setType(TestType.WEEKLY);
        testEntity.setDate(new Date());
        testEntity.setStudent(student);
        testEntity.setQuestions(questions);
        return testEntityRepository.save(testEntity);
    }

    /**
     * Cria e salva um Performance no banco de dados.
     */
    public Performance createAndSavePerformance(Student student, Content content) {
        Performance performance = new Performance();
        performance.setStudent(student);
        performance.setContent(content);
        performance.setPerformanceRate(0.8);
        performance.setCreatedAt(LocalDateTime.now());
        return performanceRepository.save(performance);
    }

    /**
     * Cria e salva um WeeklyReport no banco de dados.
     */
    public WeeklyReport createAndSaveWeeklyReport(Student student, Set<Content> contents) {
        WeeklyReport weeklyReport = new WeeklyReport();
        weeklyReport.setDate(new Date());
        weeklyReport.setStudent(student);
        weeklyReport.setContents(contents);
        return weeklyReportRepository.save(weeklyReport);
    }
}