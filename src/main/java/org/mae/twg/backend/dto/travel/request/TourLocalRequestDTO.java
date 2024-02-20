package org.mae.twg.backend.dto.travel.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class TourLocalRequestDTO implements Serializable {
    String title;
    String description;
}
