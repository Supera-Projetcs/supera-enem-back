package com.supera.enem.domain;
import com.supera.enem.domain.enums.TestType;
import com.supera.enem.exception.GlobalExceptionHandler;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "test")
public class TestEntity {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestType type;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "test_questions",
            joinColumns = @JoinColumn(name = "test_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "testEntity", fetch = FetchType.EAGER)
    private List<Answer> answers = new ArrayList<>();

    public int getTotalQuestions() {
        return this.questions != null ? this.questions.size() : 0;
    }

    public int getTotalCorrectAnswers() {
        if (this.answers == null || this.answers.isEmpty()) {
            return 0;
        }
        return (int) this.answers.stream()
                .filter(Answer::isCorrect)
                .count();
    }

    @PrePersist
    private void setDefaultDate() {
        this.date = new Date();
    }

    public boolean areAllQuestionsAnswered() {
        if (questions == null || answers == null) {
            logger.warn("Perguntas ou respostas são nulas");
            return false;
        }

        logger.info("Perguntas: {}", questions);
        logger.info("Respostas: {}", answers);

        Set<Long> questionIds = questions.stream()
                .map(Question::getId)
                .collect(Collectors.toSet());

        Set<Long> answeredQuestionIds = answers.stream()
                .map(answer -> answer.getQuestion().getId())
                .collect(Collectors.toSet());

        return answeredQuestionIds.containsAll(questionIds);
    }

    public Set<Content> getUniqueContents() {
        if (this.questions == null || this.questions.isEmpty()) {
            return Set.of();
        }
        return this.questions.stream()
                .flatMap(question -> question.getContents().stream())
                .collect(Collectors.toSet());
    }
}
