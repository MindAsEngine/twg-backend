package org.mae.twg.backend.dto.news;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.news.News;
import org.mae.twg.backend.models.news.NewsLocal;
import org.mae.twg.backend.models.news.NewsMedia;
import org.mae.twg.backend.models.travel.enums.Localization;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class NewsDTO implements ModelDTO {
    private Long id;
    private String slug;
    private String title;
    private String description;
    private LocalDate createdAt;
    private Localization localization;
    private List<String> medias;
    public NewsDTO(News news, Localization localization) {
        this.id = news.getId();
        this.slug = news.getSlug();
        this.createdAt = news.getCreatedAt();
        NewsLocal cur_local =
                news.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("News "
                        + localization.name() + " localization not found"));
        this.title = cur_local.getTitle();
        this.description = cur_local.getDescription();
        this.medias = news.getMedias().stream().map(NewsMedia::getMediaPath).toList();
        this.localization = localization;
    }

    static public NewsDTO getDTO(News news, Localization localization) {
        if (news == null || news.getIsDeleted()) {
            return null;
        }
        return new NewsDTO(news, localization);
    }
}
