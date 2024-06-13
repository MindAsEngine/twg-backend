package org.mae.twg.backend.controllers;

import org.mae.twg.backend.dto.PageDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.springframework.http.ResponseEntity;

public interface Controller<ResponseDTO, LocalReqDTO> {
    ResponseEntity<PageDTO<ResponseDTO>> getAll(Localization local, Integer page, Integer size);

    ResponseEntity<String> deleteById(Localization local, Long id);

    ResponseEntity<ResponseDTO> create(Localization local,
                                       LocalReqDTO modelDTO);

    ResponseEntity<ResponseDTO> addLocal(Localization local,
                               Long id,
                               LocalReqDTO localDTO);

    ResponseEntity<ResponseDTO> updateLocal(Localization local,
                                  Long id,
                                  LocalReqDTO localDTO);
}
