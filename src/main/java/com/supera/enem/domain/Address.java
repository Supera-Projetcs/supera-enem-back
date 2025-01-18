package com.supera.enem.domain;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, name = "house_number")
    private String houseNumber;
    private String street;

    private String city;

    private String state;
    @Column(nullable = false, name = "zip_code")
    private String zipCode;
}
