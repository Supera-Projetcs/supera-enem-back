package com.supera.enem.controller.DTOS.Student;

import com.supera.enem.controller.DTOS.AddressDTO;
import com.supera.enem.domain.enums.Weekday;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStudentDTO {
    private String name;
    private String dreamCourse;
    private String phone;
    private LocalDate birthDate;
    private AddressDTO address;
    private Set<Weekday> preferredStudyDays;
}
