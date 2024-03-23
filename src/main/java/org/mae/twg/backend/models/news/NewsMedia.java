package org.mae.twg.backend.models.news;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.Media;
import org.mae.twg.backend.models.Model;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "news_medias")
public class NewsMedia implements Media {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "news_media_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @NonNull
    @Column(name = "media_path")
    private String mediaPath;

    @Override
    public Model getModel() {
        return news;
    }
}
