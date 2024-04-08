package org.mae.twg.backend.repositories.travel;

import org.mae.twg.backend.models.travel.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepo extends JpaRepository<Hotel, Long> {
    Optional<Hotel> findBySlug(String slug);

    @Query(value = """
            select distinct on (h.hotel_id) h.*
            from hotels h
            left join resorts r on h.resort_id = r.resort_id
            where 
                (:countries is null or r.country_id in :countries)
                and
                (:resorts is null or r.resort_id in :resorts)
            """, nativeQuery = true,
            countQuery = """
            select count(*) from (select distinct on (h.hotel_id) h.*
            from hotels h
            left join resorts r on h.resort_id = r.resort_id
            where 
                (:countries is null or r.country_id in :countries)
                and
                (:resorts is null or r.resort_id in :resorts)) as src
    """)
    Page<Hotel> findAllByFilters(@Param("resorts") List<Long> resortIds,
                                 @Param("countries") List<Long> countryIds,
                                 Pageable pageable);
}
