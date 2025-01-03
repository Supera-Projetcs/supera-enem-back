package com.supera.enem.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Subject {
    @Id
    private Long id;
    private String name;
}
