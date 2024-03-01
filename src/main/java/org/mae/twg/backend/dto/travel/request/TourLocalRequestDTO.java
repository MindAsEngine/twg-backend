package org.mae.twg.backend.dto.travel.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.LocalRequestDTO;
import org.mae.twg.backend.models.travel.Tour;

@Data
@AllArgsConstructor
public class TourLocalRequestDTO implements LocalRequestDTO<Tour> {
    String title;
    String description;
}
