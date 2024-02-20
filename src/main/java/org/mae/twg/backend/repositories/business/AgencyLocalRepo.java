package org.mae.twg.backend.repositories.business;

import org.mae.twg.backend.models.business.AgencyLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgencyLocalRepo extends JpaRepository<AgencyLocal, Long> {
}
