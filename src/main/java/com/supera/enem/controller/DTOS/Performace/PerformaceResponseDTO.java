package com.supera.enem.controller.DTOS.Performace;

import com.supera.enem.controller.DTOS.ContentDTO;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PerformaceResponseDTO {
    private Integer id;
    private Double performanceRate;
    private LocalDateTime createdAt;
    private ContentDTO content;
}
