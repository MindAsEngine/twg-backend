package org.mae.twg.backend.services;

import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.models.travel.enums.Localization;

import java.util.List;

public interface TravelService<ResponseDTO extends ModelDTO, LocalDTO> {
//    List<ResponseDTO> getAll(Localization localization);
    List<ResponseDTO> getAllPaged(Localization localization, Integer page, Integer size);
    void deleteById(Long id);
    ResponseDTO create(LocalDTO requestDTO, Localization localization);
    ResponseDTO addLocal(Long id, LocalDTO requestDTO, Localization localization);
    ResponseDTO updateLocal(Long id, LocalDTO requestDTO, Localization localization);
}
