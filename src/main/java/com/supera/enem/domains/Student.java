package com.supera.enem.domains;

import com.supera.enem.domains.enums.Weekday;
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

    private String name;

    private String dreamCourse;

    private String phone;

    private String email;

    private LocalDate birthDate;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Weekday> preferredStudyDays;

    private boolean isRegistered;
}
