package org.mae.twg.backend.models.travel.media;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.mae.twg.backend.models.travel.Hotel;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hotel_medias")
public class HotelMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "hotel_media_id")
    private Long id;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @NonNull
    @Column(name = "media_path")
    private String mediaPath;
}