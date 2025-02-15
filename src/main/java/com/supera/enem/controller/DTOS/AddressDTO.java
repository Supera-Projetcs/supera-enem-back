package com.supera.enem.controller.DTOS;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AddressDTO {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String houseNumber;
}
