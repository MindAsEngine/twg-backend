package org.mae.twg.backend.dto.travel.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class SightRequestDTO implements Serializable {
    private String name;
    private String description;
    private String address;
}
