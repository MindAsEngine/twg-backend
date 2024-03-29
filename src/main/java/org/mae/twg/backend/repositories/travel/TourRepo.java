package org.mae.twg.backend.repositories.travel;

import org.mae.twg.backend.models.travel.Tour;
import org.mae.twg.backend.models.travel.enums.Stars;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourRepo extends JpaRepository<Tour, Long> {
    Optional<Tour> findBySlug(String slug);

    @Query(value = """
select distinct on (t.tour_id)
    t.*
from tours t
join tour_hotels th on th.tour_id = t.tour_id
join twg_database.public.hotels h on h.hotel_id = th.hotel_id
where\s
    (h.longitude between :minLo and :maxLo) and
    (h.latitude between :minLa and :maxLa)
""", nativeQuery = true)
    Page<Tour> findToursByGeoData(@Param("minLo") Double minLongitude, @Param("maxLo") Double maxLongitude,
                                  @Param("minLa") Double minLatitude, @Param("maxLa") Double maxLatitude,
                                  Pageable pageable);

    @Query(value = """
select t.*
from tours t
join tour_local l on t.tour_id = l.tour_id and l.localization = :local
where regexp_like(l.title, :title, 'i')
""", nativeQuery = true)
    Page<Tour> findByTitle(@Param("local") String local,
                           @Param("title") String title,
                           Pageable pageable);

    @Query(value = """
            select distinct on (t.tour_id)
                t.*
            from tours t
            left join tour_tags tt using (tour_id)
            left join tour_hotels th using (tour_id)
            left join hotels h using (hotel_id)
            left join resorts r on r.country_id = t.country_id
            where 
                (:countries is null or t.country_id in :countries)
                and
                (:tags is null or tt.tag_id in :tags)
                and
                (:types is null or t.type in :types)
                and
                (:stars is null or h.stars in :stars)
                and
                (:resorts is null or r.resort_id in :resorts)
                and
                (:hospitals is null or t.hospital_id in :hospitals)
                and
                (t.duration is null or coalesce(:minDur <= t.duration, true) and coalesce(:maxDur >= t.duration, true))
                and
                (t.price is null or coalesce(:minCost <= t.price, true) and coalesce(:maxCost >= t.price, true))
            """, nativeQuery = true)
    Page<Tour> findFilteredFours(@Param("countries") List<Long> countryIds,
                                 @Param("tags") List<Long> tagIds,
                                 @Param("hospitals") List<Long> hospitalIds,
                                 @Param("types") List<String> types,
                                 @Param("minDur") Integer minDuration,
                                 @Param("maxDur") Integer maxDuration,
                                 @Param("minCost") Long minCost,
                                 @Param("maxCost") Long maxCost,
                                 @Param("stars") List<Stars> stars,
                                 @Param("resorts") List<Long> resortIds,
                                 Pageable pageable);
}
