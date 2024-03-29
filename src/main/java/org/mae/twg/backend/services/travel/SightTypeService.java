package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.travel.request.locals.SightTypeLocalDTO;
import org.mae.twg.backend.dto.travel.response.SightTypeDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.SightType;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.SightTypeLocal;
import org.mae.twg.backend.repositories.travel.SightTypeRepo;
import org.mae.twg.backend.repositories.travel.localization.SightTypeLocalRepo;
import org.mae.twg.backend.services.TravelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SightTypeService implements TravelService<SightTypeDTO, SightTypeLocalDTO> {
    private final SightTypeRepo sightTypeRepo;
    private final SightTypeLocalRepo localRepo;

    public SightType findById(Long id) {
        SightType property = sightTypeRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("SightType with id=" + id + " not found"));
        if (property.getIsDeleted()) {
            throw new ObjectNotFoundException("SightType with id=" + id + " marked as deleted");
        }
        return property;
    }

    private List<SightTypeDTO> modelsToDTOs(Stream<SightType> sightTypes, Localization localization) {
        List<SightTypeDTO> sightTypeDTOS = sightTypes
                .filter(sightType -> !sightType.getIsDeleted())
                .filter(sightType -> sightType.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(sightType -> new SightTypeDTO(sightType, localization))
                .toList();
        if (sightTypeDTOS.isEmpty()) {
            throw new ObjectNotFoundException("SightTypes with " + localization + " localization not found");
        }
        return sightTypeDTOS;
    }

    public List<SightTypeDTO> getAll(Localization localization) {
        List<SightType> sightTypes = sightTypeRepo.findAll();
        return modelsToDTOs(sightTypes.stream(), localization);
    }

    public List<SightTypeDTO> getAllPaged(Localization localization, int page, int size) {
        Pageable sightTypePage = PageRequest.of(page, size);
        Page<SightType> sightTypes = sightTypeRepo.findAll(sightTypePage);
        return modelsToDTOs(sightTypes.stream(), localization);
    }

    @Transactional
    public void deleteById(Long id) {
        SightType sightType = findById(id);
        sightType.setIsDeleted(true);
        sightTypeRepo.save(sightType);
    }

    @Transactional
    public SightTypeDTO create(SightTypeLocalDTO sightTypeDTO, Localization localization) {
        SightType sightType = new SightType();
        sightTypeRepo.saveAndFlush(sightType);

        SightTypeLocal local = new SightTypeLocal(sightTypeDTO.getName(), localization);
        local = localRepo.saveAndFlush(local);
        sightType.addLocal(local);
        return new SightTypeDTO(sightType, localization);
    }

    @Transactional
    public SightTypeDTO addLocal(Long id, SightTypeLocalDTO sightTypeDTO, Localization localization) {
        SightType sightType = findById(id);
        boolean isExists = sightType.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            throw new ObjectAlreadyExistsException(
                    localization + " localization for SightType with id=" + id + " already exists");
        }

        SightTypeLocal sightTypeLocal =
                new SightTypeLocal(sightTypeDTO.getName(), localization);
        sightTypeLocal = localRepo.saveAndFlush(sightTypeLocal);
        sightType.addLocal(sightTypeLocal);
        return new SightTypeDTO(sightType, localization);
    }

    @Transactional
    public SightTypeDTO updateLocal(Long id, SightTypeLocalDTO sightTypeDTO, Localization localization) {
        SightType sightType = findById(id);
        SightTypeLocal cur_local =
                sightType.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException(
                                localization + " localization for sightType with id=" + id + " not found"));
        cur_local.setName(sightTypeDTO.getName());
        localRepo.saveAndFlush(cur_local);
        return new SightTypeDTO(sightType, localization);
    }
}
