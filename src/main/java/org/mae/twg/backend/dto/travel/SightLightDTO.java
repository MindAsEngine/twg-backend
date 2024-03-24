package org.mae.twg.backend.dto.travel;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Sight;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.SightLocal;

@Data
@AllArgsConstructor
public class SightLightDTO implements ModelDTO {
    private Long id;
    private String slug;
    private String name;
    private Localization localization;

    public SightLightDTO(Sight sight, Localization localization) {
        this.id = sight.getId();
        this.slug = sight.getSlug();
        SightLocal cur_local =
                sight.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Sight "
                                + localization.name() + " localization not found"));
        this.name = cur_local.getName();
        this.localization = localization;
    }

    static public SightLightDTO getDTO(Sight sight, Localization localization) {
        if (sight == null || sight.getIsDeleted()) {
            return null;
        }
        return new SightLightDTO(sight, localization);
    }
}
