package org.mae.twg.backend.repositories.business;

import org.mae.twg.backend.models.business.Agency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgencyRepo extends JpaRepository<Agency, Long> {
    Page<Agency> findAllByIsDeletedFalse(Pageable pageable);
}
