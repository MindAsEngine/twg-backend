package org.mae.twg.backend.models.travel.media;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.Media;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.Hotel;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "hotel_medias")
public class HotelMedia implements Media {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "hotel_media_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @NonNull
    @Column(name = "media_path")
    private String mediaPath;

    @Override
    public Model getModel() {
        return hotel;
    }

}
