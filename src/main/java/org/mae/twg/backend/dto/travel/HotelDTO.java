package org.mae.twg.backend.dto.travel;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Hotel;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.HotelLocal;
import org.mae.twg.backend.models.travel.media.HotelMedia;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class HotelDTO implements Serializable {
    private Long id;
    private String name;
    private String city;
    private String description;
    private String address;
    private Localization localization;
    private List<String> medias;
    private List<PropertyDTO> properties;
    private List<SightLightDTO> sights;

    public HotelDTO(Hotel hotel, Localization localization) {
        this.id = hotel.getId();
        HotelLocal cur_local =
                hotel.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Hotel "
                                + localization.name() + " localization not found"));
        this.name = cur_local.getName();
        this.city = cur_local.getCity();
        this.description = cur_local.getDescription();
        this.address = cur_local.getAddress();
        this.medias = hotel.getMedias().stream().map(HotelMedia::getMediaPath).toList();
        this.properties = hotel.getProperties().stream()
                .map(property -> new PropertyDTO(property, localization))
                .toList();
        this.sights = hotel.getSights().stream()
                .map(sight -> new SightLightDTO(sight, localization))
                .toList();
        this.localization = localization;
    }
}
