package org.mae.twg.backend.dto.travel;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Hotel;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.enums.Stars;
import org.mae.twg.backend.models.travel.localization.HotelLocal;
import org.mae.twg.backend.models.travel.media.HotelMedia;

import java.util.List;

@Data
@AllArgsConstructor
public class HotelDTO implements ModelDTO<Hotel> {
    private Long id;
    private String slug;
    private String name;
    private String city;
    private Stars stars;
    private String description;
    private String address;
    private Localization localization;
    private List<String> medias;
    private List<PropertyDTO> properties;
    private List<SightLightDTO> sights;

    public HotelDTO(Hotel hotel, Localization localization) {
        this.id = hotel.getId();
        this.slug = hotel.getSlug();
        HotelLocal cur_local =
                hotel.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Hotel "
                                + localization.name() + " localization not found"));
        this.name = cur_local.getName();
        this.city = cur_local.getCity();
        this.stars = hotel.getStars();
        this.description = cur_local.getDescription();
        this.address = cur_local.getAddress();
        this.medias = hotel.getMedias().stream().map(HotelMedia::getMediaPath).toList();
        this.properties = hotel.getProperties().stream()
                .map(property -> PropertyDTO.getDTO(property, localization))
                .toList();
        this.sights = hotel.getSights().stream()
                .map(sight -> SightLightDTO.getDTO(sight, localization))
                .toList();
        this.localization = localization;
    }

    static public HotelDTO getDTO(Hotel hotel, Localization localization) {
        if (hotel == null || hotel.getIsDeleted()) {
            return null;
        }
        return new HotelDTO(hotel, localization);
    }
}
