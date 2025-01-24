package com.supera.enem.controller.DTOS;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlitaRequestDTO {

    @JsonProperty("ID")
    private Long ID;
    @JsonProperty("Classe")
    private String Classe;
    @JsonProperty("Subclasse")
    private String Subclasse;
    @JsonProperty("Desempenho")
    private Double Desempenho;
    @JsonProperty("Peso_da_classe")
    private Double Peso_da_classe;

    @JsonProperty("Peso_da_subclasse")
    private Double Peso_da_subclasse;
    @JsonProperty("Peso_por_questao")
    private Double Peso_por_questao;

}
