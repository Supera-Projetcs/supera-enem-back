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
    private Long id;
    @JsonProperty("Classe")
    private String classe;
    @JsonProperty("Subclasse")
    private String subclasse;
    @JsonProperty("Desempenho")
    private Double desempenho;
    @JsonProperty("Peso_da_classe")
    private Double peso_da_classe;

    @JsonProperty("Peso_da_subclasse")
    private Double peso_da_subclasse;
    @JsonProperty("Peso_por_questao")
    private Double peso_por_questao;

}
