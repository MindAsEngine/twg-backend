package org.mae.twg.backend.repositories.travel;

import org.mae.twg.backend.models.travel.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepo extends JpaRepository<Property, Long> {
}
