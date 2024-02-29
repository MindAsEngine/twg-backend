package org.mae.twg.backend.services;

import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.models.travel.enums.Localization;

import java.util.List;

public interface TravelService<ModelReqDTO, LocalReqDTO> {
    List<? extends ModelDTO> getAll(Localization localization);
    void deleteById(Long id);
    ModelDTO create(ModelReqDTO requestDTO, Localization localization);
    ModelDTO addLocal(Long id, LocalReqDTO requestDTO, Localization localization);
    ModelDTO updateLocal(Long id, LocalReqDTO requestDTO, Localization localization);
}
