package com.supera.enem.controller.DTOS;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AddressDTO {
    @NotNull(message = "Rua não pode ser nulo.")
    private String street;
    @NotNull(message = "Cidade não pode ser nulo.")
    private String city;
    @NotNull(message = "Estado não pode ser nulo.")
    private String state;
    @NotNull(message = "Bairro não pode ser nulo.")
    private String neighborhood;
    @NotNull(message = "CEP não pode ser nulo.")
    private String zipCode;
    @NotNull(message = "Número não pode ser nulo.")
    private String houseNumber;
}
