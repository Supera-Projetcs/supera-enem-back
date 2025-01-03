package com.supera.enem.controller.DTOS;

import com.supera.enem.domain.enums.Weekday;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class StudentDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String dreamCourse;
    private String phone;
    private String email;
    private LocalDate birthDate;
    private AddressDTO address;
    private Set<Weekday> preferredStudyDays;
}
