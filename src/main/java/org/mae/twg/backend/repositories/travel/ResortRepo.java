package org.mae.twg.backend.repositories.travel;

import org.mae.twg.backend.models.travel.Resort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResortRepo extends JpaRepository<Resort, Long> {

    @Query(value = """
            select distinct on (r.resort_id) r.*
            from resorts r
            where (:countries is null or r.country_id in :countries)
            """, nativeQuery = true,
            countQuery = """
            select count(*) from (select distinct on (r.resort_id) r.*
            from resorts r
            where (:countries is null or r.country_id in :countries)) as src
    """)
    Page<Resort> findAllByFilters(@Param("countries") List<Long> countryIds, Pageable pageable);
    List<Resort> findAllByCountry_IdIn(List<Long> countryIds, Pageable pageable);
}
