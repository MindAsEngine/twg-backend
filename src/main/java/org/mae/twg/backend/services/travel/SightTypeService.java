package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class SightTypeService implements TravelService<SightTypeDTO, SightTypeLocalDTO> {
    private final SightTypeRepo sightTypeRepo;
    private final SightTypeLocalRepo localRepo;

    public SightType findById(Long id) {
        log.debug("Start SightTypeService.findById");
        SightType property = sightTypeRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("SightType with id=" + id + " not found");
                    return new ObjectNotFoundException("SightType with id=" + id + " not found");
                });
        if (property.getIsDeleted()) {
            log.error("SightType with id=" + id + " marked as deleted");
            throw new ObjectNotFoundException("SightType with id=" + id + " marked as deleted");
        }
        log.debug("End SightTypeService.findById");
        return property;
    }

    private List<SightTypeDTO> modelsToDTOs(Stream<SightType> sightTypes, Localization localization) {
        log.debug("Start SightTypeService.modelsToDTOs");
        List<SightTypeDTO> sightTypeDTOS = sightTypes
                .filter(sightType -> !sightType.getIsDeleted())
                .filter(sightType -> sightType.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(sightType -> new SightTypeDTO(sightType, localization))
                .toList();
        if (sightTypeDTOS.isEmpty()) {
            log.error("SightTypes with " + localization + " localization not found");
            throw new ObjectNotFoundException("SightTypes with " + localization + " localization not found");
        }
        log.debug("End SightTypeService.modelsToDTOs");
        return sightTypeDTOS;
    }

//    public List<SightTypeDTO> getAll(Localization localization) {
//        log.debug("Start SightTypeService.getAll");
//        List<SightType> sightTypes = sightTypeRepo.findAll();
//        log.debug("End SightTypeService.getAll");
//        return modelsToDTOs(sightTypes.stream(), localization);
//    }

    public List<SightTypeDTO> getAllPaged(Localization localization, Integer page, Integer size) {
        log.debug("Start SightTypeService.getAllPaged");
        Pageable sightTypePage = null;
        if (page != null && size != null) {
            sightTypePage = PageRequest.of(page, size);
        }
        Page<SightType> sightTypes = sightTypeRepo.findAllByIsDeletedFalse(sightTypePage);
        log.debug("End SightTypeService.getAllPaged");
        return modelsToDTOs(sightTypes.stream(), localization);
    }

    @Transactional
    public void deleteById(Long id) {
        log.debug("Start SightTypeService.deleteById");
        SightType sightType = findById(id);
        sightType.setIsDeleted(true);
        sightTypeRepo.save(sightType);
        log.debug("End SightTypeService.deleteById");
    }

    @Transactional
    public SightTypeDTO create(SightTypeLocalDTO sightTypeDTO, Localization localization) {
        log.debug("Start SightTypeService.create");
        SightType sightType = new SightType();
        sightTypeRepo.saveAndFlush(sightType);

        SightTypeLocal local = new SightTypeLocal(sightTypeDTO.getName(), localization);
        local = localRepo.saveAndFlush(local);
        sightType.addLocal(local);
        log.debug("End SightTypeService.create");
        return new SightTypeDTO(sightType, localization);
    }

    @Transactional
    public SightTypeDTO addLocal(Long id, SightTypeLocalDTO sightTypeDTO, Localization localization) {
        log.debug("Start SightTypeService.addLocal");
        SightType sightType = findById(id);
        boolean isExists = sightType.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            log.error(localization + " localization for SightType with id=" + id + " already exists");
            throw new ObjectAlreadyExistsException(
                    localization + " localization for SightType with id=" + id + " already exists");
        }

        SightTypeLocal sightTypeLocal =
                new SightTypeLocal(sightTypeDTO.getName(), localization);
        sightTypeLocal = localRepo.saveAndFlush(sightTypeLocal);
        sightType.addLocal(sightTypeLocal);
        log.debug("End SightTypeService.addLocal");
        return new SightTypeDTO(sightType, localization);
    }

    @Transactional
    public SightTypeDTO updateLocal(Long id, SightTypeLocalDTO sightTypeDTO, Localization localization) {
        log.debug("Start SightTypeService.updateLocal");
        SightType sightType = findById(id);
        SightTypeLocal cur_local =
                sightType.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> {
                            log.error(localization + " localization for sightType with id=" + id + " not found");
                            return new ObjectNotFoundException(
                                    localization + " localization for sightType with id=" + id + " not found");
                        });
        cur_local.setName(sightTypeDTO.getName());
        localRepo.saveAndFlush(cur_local);
        log.debug("End SightTypeService.updateLocal");
        return new SightTypeDTO(sightType, localization);
    }
}
