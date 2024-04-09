package org.mae.twg.backend.repositories.travel;

import org.mae.twg.backend.models.travel.Sight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SightRepo extends JpaRepository<Sight, Long> {
    Optional<Sight> findBySlug(String slug);

    @Query(value = """
                select
                    s.*
                from sights s
                where (s.longitude between :minLo and :maxLo)
                    and (s.latitude between :minLa and :maxLa)
            """, nativeQuery = true)
    Page<Sight> findByGeoData(@Param("minLo") Double minLongitude, @Param("maxLo") Double maxLongitude,
                              @Param("minLa") Double minLatitude, @Param("maxLa") Double maxLatitude,
                              Pageable pageable);
}
