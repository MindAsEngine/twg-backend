package org.mae.twg.backend.dto.travel.response.lightDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Property;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.PropertyLocal;

@Data
@AllArgsConstructor
@Log4j2
public class PropertyLightDTO implements ModelDTO {
    private Long id;
    private String title;
    private Localization localization;

    public PropertyLightDTO(Property property, Localization localization) {
        log.debug("start PropertyLightDTO constructor");
        this.id = property.getId();
        PropertyLocal cur_local =
                property.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Property '"
                                + localization.name() + "' localization not found"));
        this.title = cur_local.getTitle();
        this.localization = localization;
        log.debug("end PropertyLightDTO constructor");
    }

    static public PropertyLightDTO getDTO(Property property, Localization localization) {
        log.debug("start PropertyLightDTO.getDTO");
        if (property == null || property.getIsDeleted()) {
            return null;
        }
        log.debug("end PropertyLightDTO.getDTO");
        return new PropertyLightDTO(property, localization);
    }
}
