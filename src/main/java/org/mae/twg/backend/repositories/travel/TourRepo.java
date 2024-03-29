package org.mae.twg.backend.repositories.travel;

import org.mae.twg.backend.models.travel.Tour;
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
                                  @Param("minLa")Double minLatitude, @Param("maxLa")Double maxLatitude,
                                  Pageable pageable);
}
