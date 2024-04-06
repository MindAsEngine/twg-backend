package org.mae.twg.backend.dto.travel.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.SightType;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.SightTypeLocal;

@Data
@AllArgsConstructor
@Log4j2
public class SightTypeDTO implements ModelDTO {
    private Long id;
    private String name;
    private String description;
    private Localization localization;

    public SightTypeDTO(SightType property, Localization localization) {
        log.debug("start SightTypeDTO constructor");
        this.id = property.getId();
        SightTypeLocal cur_local =
                property.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("SightType '"
                                + localization.name() + "' localization not found"));
        this.name = cur_local.getName();
        this.localization = localization;
        log.debug("end SightTypeDTO constructor");
    }

    static public SightTypeDTO getDTO(SightType property, Localization localization) {
        if (property == null || property.getIsDeleted()) {
            return null;
        }
        return new SightTypeDTO(property, localization);
    }
}
