package com.supera.enem.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double content_weight;
    private double question_weight;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToMany(mappedBy = "contents")
    @JsonBackReference
    private Set<WeeklyReport> weeklyReports;

    @ManyToMany(mappedBy = "contents")
    @JsonBackReference
    private Set<Question> questions;
}
