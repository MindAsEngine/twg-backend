package org.mae.twg.backend.repositories.travel;

import org.mae.twg.backend.models.travel.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepo extends JpaRepository<Hotel, Long> {

}
