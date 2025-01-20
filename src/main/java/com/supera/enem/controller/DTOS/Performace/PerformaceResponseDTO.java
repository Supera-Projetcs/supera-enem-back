package com.supera.enem.controller.DTOS.Performace;

import com.supera.enem.controller.DTOS.ContentDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
@Setter
@Getter
@Data
public class PerformaceResponseDTO {
    private Integer id;
    private Double performanceRate;
    private LocalDateTime createdAt;
    private ContentDTO content;
}
