package org.mae.twg.backend.repositories.travel.localization;

import org.mae.twg.backend.models.travel.localization.CountryLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryLocalRepo extends JpaRepository<CountryLocal, Long> {
}
