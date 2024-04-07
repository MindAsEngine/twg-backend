package org.mae.twg.backend.dto.travel.response.lightDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Sight;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.SightLocal;

@Data
@AllArgsConstructor
@Log4j2
public class SightLightDTO implements ModelDTO {
    private Long id;
    private String slug;
    private String name;
    private Double latitude;
    private Double longitude;
    private Localization localization;

    public SightLightDTO(Sight sight, Localization localization) {
        log.debug("start SightLightDTO constructor");
        this.id = sight.getId();
        this.slug = sight.getSlug();
        SightLocal cur_local =
                sight.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Sight "
                                + localization.name() + " localization not found"));
        this.name = cur_local.getName();
        this.latitude = sight.getLatitude();
        this.longitude = sight.getLongitude();
        this.localization = localization;
        log.debug("end SightLightDTO constructor");
    }

    static public SightLightDTO getDTO(Sight sight, Localization localization) {
        log.debug("start SightLightDTO.getDTO");
        if (sight == null || sight.getIsDeleted()) {
            return null;
        }
        log.debug("end SightLightDTO.getDTO");
        return new SightLightDTO(sight, localization);
    }
}
