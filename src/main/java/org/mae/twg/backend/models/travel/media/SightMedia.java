package org.mae.twg.backend.models.travel.media;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.travel.Sight;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sight_medias")
public class SightMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sight_media_id")
    private Long id;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sight_id", nullable = false)
    private Sight sight;

    @NonNull
    @Column(name = "media_path")
    private String mediaPath;
}
