package org.mae.twg.backend.repositories.travel;

import org.mae.twg.backend.models.travel.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepo extends JpaRepository<Country, Long> {

    Page<Country> findAllByIsDeletedFalse(Pageable pageable);
    @Query(value = """
    select distinct on (c.country_id)
                c.*
            from countries c
            join tours t using (country_id)
            where (:types is null or t.type in :types)
    """, nativeQuery = true,
    countQuery = """
    select count(*) from (select distinct on (c.country_id)
                c.*
            from countries c
            join tours t using (country_id)
            where (:types is null or t.type in :types)) as src
    """)
    Page<Country> findAllByTourType(@Param("types") List<String> tourTypes, Pageable pageable);
}
