package com.supera.enem.domain;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

@Entity
public class WeeklyReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToMany
    @JoinTable(
            name = "weekly_report_content",
            joinColumns = @JoinColumn(name = "weekly_report_id"),
            inverseJoinColumns = @JoinColumn(name = "content_id")
    )
    private Set<Content> contents;
}
