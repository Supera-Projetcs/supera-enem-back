package com.supera.enem.domain;

import com.supera.enem.domain.enums.Weekday;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, name = "keycloak_id")
    private  String keycloakId;

    @Column(nullable = false, name = "first_name")
    private String firstName;

    @Column(nullable = false, name = "last_name")
    private String lastName;

    @Column(nullable = false, name = "dream_course")
    private String dreamCourse;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, name = "birth_date")
    private LocalDate birthDate;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_preferredstudydays",
            joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "preferred_study_day")
    private Set<Weekday> preferredStudyDays;

    @Column(name = "is_registered")
    private boolean isRegistered;
}
