package org.mae.twg.backend.models.travel.localization;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.mae.twg.backend.models.travel.Hotel;
import org.mae.twg.backend.models.travel.enums.Localization;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hotel_local")
public class HotelLocal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "local_id")
    private Long id;

    @NonNull
    @Column(name = "name")
    private String name;

    @NonNull
    @Column(name = "city")
    private String city;

    @NonNull
    @Column(name = "description",
            columnDefinition = "TEXT")
    private String description;

    @NonNull
    @Column(name = "address")
    private String address;

    @Enumerated(EnumType.STRING)
    private Localization localization;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY,
            optional = false)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
}
