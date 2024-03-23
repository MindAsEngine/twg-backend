package org.mae.twg.backend.models.travel.media;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.Media;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.Sight;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "sight_medias")
public class SightMedia implements Media {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sight_media_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sight_id", nullable = false)
    private Sight sight;

    @NonNull
    @Column(name = "media_path")
    private String mediaPath;

    @Override
    public Model getModel() {
        return sight;
    }
}
