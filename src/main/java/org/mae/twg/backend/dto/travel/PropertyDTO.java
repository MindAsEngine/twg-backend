package org.mae.twg.backend.dto.travel;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Property;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.PropertyLocal;

@Data
@AllArgsConstructor
public class PropertyDTO implements ModelDTO<Property> {
    private Long id;
    private String title;
    private String description;
    private Localization localization;

    public PropertyDTO(Property property, Localization localization) {
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
    }

    static public PropertyDTO getDTO(Property property, Localization localization) {
        if (property == null || property.getIsDeleted()) {
            return null;
        }
        return new PropertyDTO(property, localization);
    }
}
