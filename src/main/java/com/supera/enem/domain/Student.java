package com.supera.enem.domain;

import com.supera.enem.domain.enums.Weekday;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, name = "keycloak_id")
    private  String keycloakId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, name = "name")
    private String name;


    @Column(nullable = false, name = "dream_course")
    private String dreamCourse;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, unique = true)
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

    @Column(name = "onboarding_done", nullable = false)
    private boolean onboardingDone = false;

}
