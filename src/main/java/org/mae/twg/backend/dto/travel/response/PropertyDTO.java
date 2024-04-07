package org.mae.twg.backend.dto.travel.response;

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
public class PropertyDTO implements ModelDTO {
    private Long id;
    private String title;
    private String description;
    private Localization localization;

    public PropertyDTO(Property property, Localization localization) {
        log.debug("start PropertyDTO constructor");
        this.id = property.getId();
        PropertyLocal cur_local =
                property.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Property '"
                                + localization.name() + "' localization not found"));
        this.title = cur_local.getTitle();
        this.description = cur_local.getDescription();
        this.localization = localization;
        log.debug("end PropertyDTO constructor");
    }

    static public PropertyDTO getDTO(Property property, Localization localization) {
        log.debug("start PropertyDTO.getDTO");
        if (property == null || property.getIsDeleted()) {
            return null;
        }
        log.debug("end PropertyDTO.getDTO");
        return new PropertyDTO(property, localization);
    }
}
