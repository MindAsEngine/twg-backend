package org.mae.twg.backend.dto.travel;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.ModelLightDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Resort;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.ResortLocal;

@Data
@AllArgsConstructor
public class ResortLightDTO implements ModelLightDTO<Resort> {
    private Long id;
    private String slug;
    private String name;
//    private String description;
    private Localization localization;
//    private List<String> medias;

    public ResortLightDTO(Resort resort, Localization localization) {
        this.id = resort.getId();
        this.slug = resort.getSlug();
        ResortLocal cur_local =
                resort.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Resort "
                                + localization.name() + " localization not found"));
        this.name = cur_local.getName();
//        this.description = cur_local.getDescription();
//        this.medias = resort.getMedias().stream().map(ResortMedia::getMediaPath).toList();
        this.localization = localization;
    }

    static public ResortLightDTO getDTO(Resort resort, Localization localization) {
        if (resort == null || resort.getIsDeleted()) {
            return null;
        }
        return new ResortLightDTO(resort, localization);
    }
}
