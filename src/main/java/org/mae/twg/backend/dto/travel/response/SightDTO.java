package org.mae.twg.backend.dto.travel.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Sight;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.SightLocal;
import org.mae.twg.backend.models.travel.media.SightMedia;

import java.util.List;

@Data
@AllArgsConstructor
public class SightDTO implements ModelDTO {
    private Long id;
    private String slug;
    private String name;
    private String introduction;
    private String description;
    private String address;
    private Double grade;
    private Long commentAmount;
    private Double latitude;
    private Double longitude;
    private Localization localization;
    private List<String> medias;

    public SightDTO(Sight sight, Localization localization) {
        this.id = sight.getId();
        this.slug = sight.getSlug();
        SightLocal cur_local =
                sight.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Sight "
                                + localization.name() + " localization not found"));
        this.name = cur_local.getName();
        this.introduction = cur_local.getIntroduction();
        this.description = cur_local.getDescription();
        this.address = cur_local.getAddress();
        this.latitude = sight.getLatitude();
        this.longitude = sight.getLongitude();
        this.medias = sight.getMedias().stream().map(SightMedia::getMediaPath).toList();
        this.localization = localization;
    }

    static public SightDTO getDTO(Sight sight, Localization localization) {
        if (sight == null || sight.getIsDeleted()) {
            return null;
        }
        return new SightDTO(sight, localization);
    }
}
