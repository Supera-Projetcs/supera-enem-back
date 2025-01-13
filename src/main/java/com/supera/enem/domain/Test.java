package com.supera.enem.domain;
import com.supera.enem.domain.enums.TestType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Test {

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

    @ManyToMany
    @JoinTable(
            name = "test_questions",
            joinColumns = @JoinColumn(name = "test_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions;

    @PrePersist
    private void setDefaultDate() {
        this.date = new Date();
    }
}
