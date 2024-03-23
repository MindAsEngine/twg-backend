package org.mae.twg.backend.models.travel.media;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.Media;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.Resort;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "resort_medias")
public class ResortMedia implements Media {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "resort_media_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resort_id", nullable = false)
    private Resort resort;

    @NonNull
    @Column(name = "media_path")
    private String mediaPath;

    @Override
    public Model getModel() {
        return resort;
    }
}
