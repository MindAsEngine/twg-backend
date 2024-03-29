package org.mae.twg.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeData {
    private Long id;
    private Double grade;
    private Long count;
}
