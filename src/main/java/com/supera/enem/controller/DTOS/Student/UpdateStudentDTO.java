package com.supera.enem.controller.DTOS.Student;

import com.supera.enem.controller.DTOS.AddressDTO;
import com.supera.enem.domain.enums.Weekday;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateStudentDTO {
    private String firstName;
    private String lastName;
    private String dreamCourse;
    private String phone;
    private LocalDate birthDate;
    private AddressDTO address;
    private Set<Weekday> preferredStudyDays;
}
