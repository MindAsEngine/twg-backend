package org.mae.twg.backend.repositories.travel;

import org.mae.twg.backend.models.travel.Hospital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalRepo extends JpaRepository<Hospital, Long> {
    Optional<Hospital> findBySlug(String slug);

    @Query(value = """
                select
                    h.*
                from hospitals h
                where (h.longitude between :minLo and :maxLo)
                    and (h.latitude between :minLa and :maxLa)
                order by h.slug
            """, nativeQuery = true)
    Page<Hospital> findByGeoData(@Param("minLo") Double minLongitude, @Param("maxLo") Double maxLongitude,
                                 @Param("minLa") Double minLatitude, @Param("maxLa") Double maxLatitude,
                                 Pageable pageable);
}
