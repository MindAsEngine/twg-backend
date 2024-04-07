package org.mae.twg.backend.dto.travel.response.lightDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Hotel;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.HotelLocal;

@Data
@AllArgsConstructor
@Log4j2
public class HotelLightDTO implements ModelDTO {
    private Long id;
    private String slug;
    private String name;
    private Double latitude;
    private Double longitude;
    private Localization localization;

    public HotelLightDTO(Hotel hotel, Localization localization) {
        log.debug("start HotelLightDTO constructor");
        this.id = hotel.getId();
        this.slug = hotel.getSlug();
        HotelLocal cur_local =
                hotel.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Hotel "
                                + localization.name() + " localization not found"));
        this.name = cur_local.getName();
        this.latitude = hotel.getLatitude();
        this.longitude = hotel.getLongitude();
        this.localization = localization;
        log.debug("end HotelLightDTO constructor");
    }

    static public HotelLightDTO getDTO(Hotel hotel, Localization localization) {
        log.debug("start HotelLightDTO.getDTO");
        if (hotel == null || hotel.getIsDeleted()) {
            return null;
        }
        log.debug("end HotelLightDTO.getDTO");
        return new HotelLightDTO(hotel, localization);
    }
}
