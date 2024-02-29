package org.mae.twg.backend.controllers.travel;

import org.mae.twg.backend.models.travel.enums.Localization;
import org.springframework.http.ResponseEntity;

public interface TravelController<ModelReqDTO, LocalReqDTO> {
    ResponseEntity<?> getAll(Localization local);

    ResponseEntity<?> deleteById(Localization local, Long id);

    ResponseEntity<?> create(Localization local,
                             ModelReqDTO modelDTO);

    ResponseEntity<?> addLocal(Localization local,
                               Long id,
                               LocalReqDTO localDTO);

    ResponseEntity<?> updateLocal(Localization local,
                                  Long id,
                                  LocalReqDTO localDTO);
}
