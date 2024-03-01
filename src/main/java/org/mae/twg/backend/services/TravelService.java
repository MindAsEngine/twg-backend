package org.mae.twg.backend.services;

import org.mae.twg.backend.dto.LocalRequestDTO;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.dto.ModelRequestDTO;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.enums.Localization;

import java.util.List;

public interface TravelService<
        T extends Model,
        ModelReqDTO extends ModelRequestDTO<T>,
        LocalReqDTO extends LocalRequestDTO<T>> {
    List<? extends ModelDTO<T>> getAll(Localization localization);
    List<? extends ModelDTO<T>> getAllPaged(Localization localization, int page, int size);
    void deleteById(Long id);
    ModelDTO<T> create(ModelReqDTO requestDTO, Localization localization);
    ModelDTO<T> addLocal(Long id, LocalReqDTO requestDTO, Localization localization);
    ModelDTO<T> updateLocal(Long id, LocalReqDTO requestDTO, Localization localization);
}
