package org.mae.twg.backend.dto.business;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgencyRequestDTO {
    String name;
    String description;
    String contacts;
    String address;
}
