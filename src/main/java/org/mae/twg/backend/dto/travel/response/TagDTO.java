package org.mae.twg.backend.dto.travel.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Tag;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.TagLocal;

@Data
@AllArgsConstructor
public class TagDTO implements ModelDTO {
    private Long id;
    private String name;
    private Localization localization;

    public TagDTO(Tag tag, Localization localization) {
        this.id = tag.getId();
        TagLocal cur_local =
                tag.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Tag "
                                + localization.name() + " localization not found"));
        this.name = cur_local.getName();
        this.localization = localization;
    }

    static public TagDTO getDTO(Tag tag, Localization localization) {
        if (tag == null || tag.getIsDeleted()) {
            return null;
        }
        return new TagDTO(tag, localization);
    }
}
