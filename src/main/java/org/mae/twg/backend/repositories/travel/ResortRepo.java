package org.mae.twg.backend.repositories.travel;

import org.mae.twg.backend.models.travel.Resort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResortRepo extends JpaRepository<Resort, Long> {
    List<Resort> findAllByCountry_IdIn(List<Long> countryIds, Pageable pageable);
}
