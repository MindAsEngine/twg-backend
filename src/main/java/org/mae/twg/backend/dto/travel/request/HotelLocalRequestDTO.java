package org.mae.twg.backend.dto.travel.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class HotelLocalRequestDTO implements Serializable {
    private String name;
    private String city;
    private String description;
    private String address;
}
