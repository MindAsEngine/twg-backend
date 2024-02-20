package org.mae.twg.backend.models.travel.localization;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.travel.Hotel;
import org.mae.twg.backend.models.travel.enums.Localization;

@Entity
//@Data
@Getter
@Setter
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

    @Column(name = "city")
    private String city;

    @Column(name = "description",
            columnDefinition = "TEXT")
    private String description;

    @Column(name = "address")
    private String address;

    @Enumerated(EnumType.STRING)
    private Localization localization;

    @ManyToOne(fetch = FetchType.LAZY,
            optional = false)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    public HotelLocal(@NonNull String name,
                      String city,
                      String description,
                      String address,
                      Localization localization,
                      Hotel hotel) {
        this.name = name;
        this.city = city;
        this.description = description;
        this.address = address;
        this.localization = localization;
        this.hotel = hotel;
    }
}
