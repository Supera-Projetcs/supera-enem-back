package com.supera.enem.domain;
import com.supera.enem.domain.enums.TestType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Table(name = "test")
public class TestEntity {

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "test_questions",
            joinColumns = @JoinColumn(name = "test_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "testEntity", fetch = FetchType.LAZY)
    private List<Answer> answers = new ArrayList<>();

    @PrePersist
    private void setDefaultDate() {
        this.date = new Date();
    }

    public boolean areAllQuestionsAnswered() {
        if (questions == null || answers == null) {
            return false;
        }

        Set<Long> questionIds = questions.stream()
                .map(Question::getId)
                .collect(Collectors.toSet());

        Set<Long> answeredQuestionIds = answers.stream()
                .map(answer -> answer.getQuestion().getId())
                .collect(Collectors.toSet());

        return answeredQuestionIds.containsAll(questionIds);
    }
}
