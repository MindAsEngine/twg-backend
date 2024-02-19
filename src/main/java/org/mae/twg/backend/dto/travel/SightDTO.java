package org.mae.twg.backend.dto.travel;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Sight;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.SightLocal;
import org.mae.twg.backend.models.travel.media.SightMedia;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class SightDTO implements Serializable {
    private Long id;
    private String name;
    private String description;
    private String address;
    private Localization localization;
    private List<String> medias;

    public SightDTO(Sight sight, Localization localization) {
        this.id = sight.getId();
        SightLocal cur_local =
                sight.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Sight "
                                + localization.name() + " localization not found"));
        this.name = cur_local.getName();
        this.description = cur_local.getDescription();
        this.address = cur_local.getAddress();
        this.medias = sight.getMedias().stream().map(SightMedia::getMediaPath).toList();
        this.localization = localization;
    }
}